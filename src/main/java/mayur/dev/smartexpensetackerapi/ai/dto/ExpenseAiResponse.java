package mayur.dev.smartexpensetackerapi.ai.dto;

import lombok.Data;

@Data
public class ExpenseAiResponse {
    private String category;
    private Double amount;
}