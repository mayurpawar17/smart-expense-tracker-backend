package mayur.dev.smartexpensetackerapi.features.expense.service;

import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponseDTO;
import mayur.dev.smartexpensetackerapi.features.category.entity.CategoryData;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseRequestDTO;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponseDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseService {

    ExpenseResponseDTO createExpense(ExpenseRequestDTO request);

    Page<ExpenseResponseDTO> getExpenses(Long userId, String search, String category, int page, int size);

    ExpenseResponseDTO getExpenseById(Long id);

    Double getTotalExpenses(Long userId);

    List<CategoryData> getCategoryAnalytics(Long userId);

    BigDecimal getCurrentMonthTotal(Long userId);

    InsightResponseDTO getMonthlyInsights(Long userId, int month, int year);

    ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request);

    void deleteExpense(Long id);

}
