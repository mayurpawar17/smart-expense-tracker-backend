package mayur.dev.smartexpensetackerapi.expense.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@JsonPropertyOrder({ "id", "title", "amount", "category", "createdAt" }) // to order the response
public class ExpenseResponse {
    private Long id;
    private String title;

    // Use a string representation for the frontend to avoid precision issues
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.00")
    private BigDecimal amount;
    private String category;

    // Standardize your date format (ISO 8601 is best practice)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}