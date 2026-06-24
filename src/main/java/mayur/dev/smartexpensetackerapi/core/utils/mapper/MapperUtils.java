package mayur.dev.smartexpensetackerapi.core.utils.mapper;


import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponseDTO;
import mayur.dev.smartexpensetackerapi.features.expense.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class MapperUtils {
    public ExpenseResponseDTO mapToExpenseResponse(Expense expense) {
        if (expense == null) {
            return null;
        }
        ExpenseResponseDTO expenseResponseDTO = new ExpenseResponseDTO();
        expenseResponseDTO.setId(expense.getId());
        expenseResponseDTO.setTitle(expense.getTitle());
        expenseResponseDTO.setAmount(expense.getAmount());
        expenseResponseDTO.setCategory(expense.getCategory());
        expenseResponseDTO.setCreatedAt(expense.getCreatedAt());
        return expenseResponseDTO;
    }

}
