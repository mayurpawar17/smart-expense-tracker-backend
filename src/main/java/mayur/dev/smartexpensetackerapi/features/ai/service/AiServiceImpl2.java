package mayur.dev.smartexpensetackerapi.features.ai.service;

import lombok.extern.slf4j.Slf4j;
import mayur.dev.smartexpensetackerapi.features.ai.dto.ExpenseAiResponseDTO;
import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponseDTO;
import mayur.dev.smartexpensetackerapi.features.category.dto.CategorySummaryRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AiServiceImpl2 implements AiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public AiServiceImpl2(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.model}")
    private String model;


    @Override
    public ExpenseAiResponseDTO extractExpense(String input) {

        String prompt = """
                Extract the following from the expense text:
                - category (Food, Travel, Shopping, Bills, Entertainment, Health, Groceries, Education, Transport, Housing, Insurance, Subscriptions, Personal Care, Gift, Savings, Debt, Other)
                - amount (number only)
                
                STRICT RULES:
                - Return ONLY JSON
                - Do NOT add explanation
                - Do NOT use markdown
                - Do NOT wrap in ```json
                
                Format:
                {"category":"Food","amount":450}
                
                Expense: %s
                """.formatted(input);

        String aiText = callGroq(prompt);

        try {
            String cleanJson = extractJson(aiText);
            return objectMapper.readValue(cleanJson, ExpenseAiResponseDTO.class);
        } catch (Exception e) {
            log.error("Failed parsing AI expense response, invoking native fallback extraction loop", e);
            ExpenseAiResponseDTO fallback = new ExpenseAiResponseDTO();
            fallback.setCategory("Other");
            fallback.setAmount(extractAmount(input));
            return fallback;
        }
    }

    @Override
    public InsightResponseDTO generateInsights(List<CategorySummaryRequestDTO> summaryList) {

        String prompt = buildPrompt(summaryList);
        String aiText = callGroq(prompt);
        try {
            String cleanJson = extractJson(aiText);
            return objectMapper.readValue(cleanJson, InsightResponseDTO.class);
        } catch (Exception e) {
            log.error("AI Insight generation failed. Serving native analytics backup schema.", e);
            return fallbackInsights(summaryList);
        }
    }

    @Override
    public String generateDescriptionFromTitle(String title) {

        String prompt = """
            Generate a very short, clean description (maximum 5-7 words) for an expense with the title: "%s".
            
            STRICT RULES:
            - Do NOT include quotes around the output.
            - Do NOT add explanations or pleasantries.
            - Provide ONLY the short description sentence.
            
            Example Title: Starbucks Coffee
            Example Output: Handcrafted coffee beverage from Starbucks cafe
            """.formatted(title);


        try {
            String aiResponse = callGroq(prompt);

            // Clean up any accidental quotes or newlines the LLM might return
            if (aiResponse != null) {
                return aiResponse.replace("\"", "").trim();
            }
            throw new RuntimeException("Empty response from Groq");

        } catch (Exception e) {
            log.error("Groq description generation failed for title: {}. Using fallback description.", title, e);
            // Fallback: Return a sensible generic description based on the title
            return "Expense recorded for " + title;
        }
    }


    private String callGroq(String prompt) {
        Map<String, Object> message = Map.of("role", "user", "content", prompt);
        Map<String, Object> body = Map.of("model", model, "messages", List.of(message), "temperature", 0.2);

        try {
            log.info("Groq API Outbound payload via WebClient: {}", objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            log.warn("Could not serialize payload tracking maps to console output");
        }

        // Construct the absolute URL string including the query parameter safely
        String fullUrlWithKey = String.format("%s?key=%s", apiUrl, apiKey);

        ResponseEntity<Map> response = webClient.post().uri(fullUrlWithKey) // Pass the full absolute URL string directly here
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(apiKey);
                }).bodyValue(body).retrieve().toEntity(Map.class).block();

        try {
            if (response != null && response.getBody() != null) {
                @SuppressWarnings("unchecked") List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                @SuppressWarnings("unchecked") Map<String, Object> messageResp = (Map<String, Object>) choices.get(0).get("message");
                return messageResp.get("content").toString();
            }
            throw new RuntimeException("Received empty response payload from Groq API");
        } catch (Exception e) {
            throw new RuntimeException("Groq API response payload structure parsing failed completely", e);
        }
    }


    private String buildPrompt(List<CategorySummaryRequestDTO> summaryList) {
        return """
                You are a financial assistant.
                
                Analyze the expense summary and return JSON:
                
                {
                  "summary": "5-6 words",
                  "topCategory": "category name",
                  "warning": "if overspending else null",
                  "suggestion": "money saving tip"
                }
                
                Rules:
                - Keep it short
                - Only JSON
                - No explanation
                - No markdown
                - Strictly 8-10 words for summary
                - Strictly 8-10 words for warning
                
                Data:
                %s
                """.formatted(summaryList.toString());
    }

    private InsightResponseDTO fallbackInsights(List<CategorySummaryRequestDTO> data) {
        InsightResponseDTO res = new InsightResponseDTO();
        CategorySummaryRequestDTO top = data.stream().max(Comparator.comparing(CategorySummaryRequestDTO::getTotalAmount)).orElse(null);

        res.setSummary("Basic spending summary generated");
        res.setTopCategory(top != null ? top.getCategory() : "Unknown");
        res.setWarning(null);
        res.setSuggestion("Try reducing spending in top category");
        return res;
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 && end != -1) {
            return text.substring(start, end + 1);
        }
        throw new RuntimeException("No valid root level JSON maps found inside text stream context.");
    }

    private Double extractAmount(String input) {
        Pattern pattern = Pattern.compile("(\\d+)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return 0.0;
    }
}
