package mayur.dev.smartexpensetackerapi.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategorySummary {
    private String category;
    private BigDecimal totalAmount;
}
