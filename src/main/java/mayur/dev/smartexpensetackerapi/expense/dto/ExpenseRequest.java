package mayur.dev.smartexpensetackerapi.expense.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Amount must be a valid monetary format")
    private BigDecimal amount;

    @NotNull(message = "Category is required")
    private String category;

    @PastOrPresent(message = "Expense date cannot be in the future")
    private LocalDateTime createdAt;

}
