//package mayur.dev.smartexpensetackerapi.features.ai.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import mayur.dev.smartexpensetackerapi.features.ai.dto.ExpenseAiResponseDTO;
//import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponseDTO;
//import mayur.dev.smartexpensetackerapi.features.category.dto.CategorySummaryRequestDTO;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import tools.jackson.databind.ObjectMapper;
//
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AiServiceImpl implements AiService {
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final ObjectMapper objectMapper;
//    @Value("${ai.api.key}")
//    private String apiKey;
//    @Value("${ai.api.url}")
//    private String apiUrl;
//
//    @Value("${ai.api.model}")
//    private String model;
//
//    public ExpenseAiResponseDTO extractExpense(String input) {
//
//        String prompt = """
//                Extract the following from the expense text:
//                - category (Food, Travel, Shopping, Bills, Entertainment, Health, Groceries, Education, Transport, Housing, Insurance, Subscriptions, Personal Care, Gift, Savings, Debt, Other)
//                - amount (number only)
//
//                STRICT RULES:
//                - Return ONLY JSON
//                - Do NOT add explanation
//                - Do NOT use markdown
//                - Do NOT wrap in ```json
//
//                Format:
//                {"category":"Food","amount":450}
//
//                Expense: %s
//                """.formatted(input);
//
//        String aiText = callGroq(prompt);
//
//        try {
//            String cleanJson = extractJson(aiText);
//            return objectMapper.readValue(cleanJson, ExpenseAiResponseDTO.class);
//        } catch (Exception e) {
//            ExpenseAiResponseDTO fallback = new ExpenseAiResponseDTO();
//            fallback.setCategory("Other");
//            fallback.setAmount(extractAmount(input));
//            return fallback;
//        }
//
////        HttpHeaders headers = new HttpHeaders();
////        headers.setContentType(MediaType.APPLICATION_JSON);
////
////        Map<String, Object> textPart = Map.of("text", prompt);
////        Map<String, Object> parts = Map.of("parts", List.of(textPart));
////        Map<String, Object> body = Map.of("contents", List.of(parts));
////
////        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
////
////        String url = apiUrl + "?key=" + apiKey;
////
////        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
////
////        try {
////            // Extract AI text
////            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
////
////            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
////
////            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");
////
////            String aiText = partsList.get(0).get("text").toString();
////
////            // Convert JSON string → DTO
//////            return objectMapper.readValue(aiText, ExpenseAiResponse.class);
////
////            try {
////                String cleanJson = extractJson(aiText);
////                return objectMapper.readValue(cleanJson, ExpenseAiResponse.class);
////
////            } catch (Exception e) {
////                // fallback logic
////                ExpenseAiResponse fallback = new ExpenseAiResponse();
////                fallback.setCategory("Other");
////
////                // try extract amount manually
////                fallback.setAmount(extractAmount(input));
////
////                return fallback;
////            }
////
////        } catch (Exception e) {
////            throw new RuntimeException("Failed to parse AI response");
////        }
//    }
//
//    public InsightResponseDTO generateInsights(List<CategorySummaryRequestDTO> summaryList) {
//
//        String prompt = buildPrompt(summaryList);
//
//        String aiText = callGroq(prompt);
//
//        try {
//            String cleanJson = extractJson(aiText);
//            return objectMapper.readValue(cleanJson, InsightResponseDTO.class);
//
//        } catch (Exception e) {
//            return fallbackInsights(summaryList);
//        }
//    }
//
//    private String callGroq(String prompt) {
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey); // Authorization: Bearer <key>
//
//        Map<String, Object> message = Map.of("role", "user", "content", prompt);
////        Map<String, Object> parts = Map.of("parts", List.of(textPart));
//        Map<String, Object> body = Map.of("model", model, "messages", List.of(message), "temperature", 0.2);
//        log.info("Groq request body: " + new ObjectMapper().writeValueAsString(body));
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//
//        String url = apiUrl + "?key=" + apiKey;
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//
//        try {
//
//            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//            Map<String, Object> messageResp = (Map<String, Object>) choices.get(0).get("message");
//            return messageResp.get("content").toString();
////
////            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
////
////            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
////
////            List<Map<String, Object>> partsList = (List<Map<String, Object>>) content.get("parts");
////
////            return partsList.get(0).get("text").toString();
//
//        } catch (Exception e) {
//            throw new RuntimeException("Groq API parsing failed");
//        }
//    }
//
//    private String buildPrompt(List<CategorySummaryRequestDTO> summaryList) {
//
//        return """
//                You are a financial assistant.
//
//                Analyze the expense summary and return JSON:
//
//                {
//                  "summary": "5-6 words",
//                  "topCategory": "category name",
//                  "warning": "if overspending else null",
//                  "suggestion": "money saving tip"
//                }
//
//                Rules:
//                - Keep it short
//                - Only JSON
//                - No explanation
//                - No markdown
//                - Strictly 8-10 words for summary
//                - Strictly 8-10 words for warning
//
//                Data:
//                %s
//                """.formatted(summaryList.toString());
//    }
//
//
//    private InsightResponseDTO fallbackInsights(List<CategorySummaryRequestDTO> data) {
//
//        InsightResponseDTO res = new InsightResponseDTO();
//
//        CategorySummaryRequestDTO top = data.stream().max(Comparator.comparing(CategorySummaryRequestDTO::getTotalAmount)).orElse(null);
//
//        res.setSummary("Basic spending summary generated");
//        res.setTopCategory(top != null ? top.getCategory() : "Unknown");
//        res.setWarning(null);
//        res.setSuggestion("Try reducing spending in top category");
//
//        return res;
//    }
//
//    private String extractJson(String text) {
//        int start = text.indexOf("{");
//        int end = text.lastIndexOf("}");
//
//        if (start != -1 && end != -1) {
//            return text.substring(start, end + 1);
//        }
//        throw new RuntimeException("No JSON found in AI response");
//    }
//
//    private Double extractAmount(String input) {
//        Pattern pattern = Pattern.compile("(\\d+)");
//        Matcher matcher = pattern.matcher(input);
//
//        if (matcher.find()) {
//            return Double.parseDouble(matcher.group(1));
//        }
//        return 0.0;
//    }
//}
//
//
