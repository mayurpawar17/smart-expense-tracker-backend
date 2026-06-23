package mayur.dev.smartexpensetackerapi.features.ai.dto;

import lombok.Data;

@Data
public class ExpenseAiResponseDTO {
    private String category;
    private Double amount;
}