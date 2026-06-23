package mayur.dev.smartexpensetackerapi.core.utils.mapper;


import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.features.expense.entity.Expense;
import org.springframework.stereotype.Component;

@Component
public class MapperUtils {
    public ExpenseResponse mapToExpenseResponse(Expense expense) {
        if (expense == null) {
            return null;
        }
        ExpenseResponse expenseResponse = new ExpenseResponse();
        expenseResponse.setId(expense.getId());
        expenseResponse.setTitle(expense.getTitle());
        expenseResponse.setAmount(expense.getAmount());
        expenseResponse.setCategory(expense.getCategory());
        expenseResponse.setCreatedAt(expense.getCreatedAt());
        return expenseResponse;
    }

}
