package mayur.dev.smartexpensetackerapi.features.ai.service;

import mayur.dev.smartexpensetackerapi.features.ai.dto.ExpenseAiResponseDTO;
import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponseDTO;
import mayur.dev.smartexpensetackerapi.features.category.dto.CategorySummaryRequestDTO;

import java.util.List;

public interface AiService {

    //Extracts structured expense category and amount from unstructured raw text inputs.
    ExpenseAiResponseDTO extractExpense(String input);

    //Generates analytical insights and actionable advice based on historical user expense summaries.
    InsightResponseDTO generateInsights(List<CategorySummaryRequestDTO> summaryList);

    //Generates a short, context-aware description based on an expense title.
    String generateDescriptionFromTitle(String title);
}
