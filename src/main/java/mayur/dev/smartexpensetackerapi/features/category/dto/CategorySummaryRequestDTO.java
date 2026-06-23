package mayur.dev.smartexpensetackerapi.features.category.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySummaryRequestDTO {
    private String category;
    private BigDecimal totalAmount;
}
