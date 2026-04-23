package mayur.dev.smartexpensetackerapi.features.ai.dto;

import lombok.Data;

@Data
public class InsightResponse {
    private String summary;
    private String topCategory;
    private String warning;
    private String suggestion;
}