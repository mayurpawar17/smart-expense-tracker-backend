package mayur.dev.smartexpensetackerapi.ai.service;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.ai.dto.ExpenseAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    @Value("${ai.api.key}")
    private String apiKey;
    @Value("${ai.api.url}")
    private String apiUrl;

    public ExpenseAiResponse extractExpense(String input) {

        String prompt = """
                Extract the following from the expense text:
                - category (Food, Travel, Shopping, Bills, Entertainment, Other)
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> parts = Map.of("parts", List.of(textPart));
        Map<String, Object> body = Map.of("contents", List.of(parts));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        String url = apiUrl + "?key=" + apiKey;

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        try {
            // Extract AI text
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.getBody().get("candidates");

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> partsList =
                    (List<Map<String, Object>>) content.get("parts");

            String aiText = partsList.get(0).get("text").toString();

            // Convert JSON string → DTO
//            return objectMapper.readValue(aiText, ExpenseAiResponse.class);

            try {
                String cleanJson = extractJson(aiText);
                return objectMapper.readValue(cleanJson, ExpenseAiResponse.class);

            } catch (Exception e) {
                // fallback logic
                ExpenseAiResponse fallback = new ExpenseAiResponse();
                fallback.setCategory("Other");

                // try extract amount manually
                fallback.setAmount(extractAmount(input));

                return fallback;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response");
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");

        if (start != -1 && end != -1) {
            return text.substring(start, end + 1);
        }
        throw new RuntimeException("No JSON found in AI response");
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