package mayur.dev.smartexpensetackerapi.features.ai.dto;

import lombok.Data;

@Data
public class InsightResponseDTO {
    private String summary;
    private String topCategory;
    private String warning;
    private String suggestion;
}