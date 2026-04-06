package mayur.dev.smartexpensetackerapi.expense.service;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.expense.dto.ExpenseRequest;
import mayur.dev.smartexpensetackerapi.expense.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.expense.entity.Expense;
import mayur.dev.smartexpensetackerapi.expense.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;


    public ExpenseResponse createExpense(ExpenseRequest request) {

        Expense expense = new Expense();
        expense.setCategory(request.getCategory().toLowerCase());
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        // Set this manually here so the database always has the correct time
        expense.setCreatedAt(LocalDateTime.now());
        ;

        Expense saved = expenseRepository.save(expense);

        return mapToResponse(saved);
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public Double getTotalExpense() {
        return expenseRepository.getTotalExpense();
    }

    public Map<String, BigDecimal> getCategoryAnalytics() {
        List<Object[]> data = expenseRepository.getCategoryWiseExpense();

        Map<String, BigDecimal> result = new HashMap<>();

        for (Object[] row : data) {
            result.put((String) row[0], (BigDecimal) row[1]);
        }

        return result;
    }

    public List<ExpenseResponse> getExpensesByCategory(String category) {
        return expenseRepository.getExpensesByCategory(category).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

//    public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
//        Expense expense = expenseRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Expense not found"));
//
//        expense.setTitle(request.getTitle());
//        expense.setAmount(request.getAmount());
//        expense.setCreatedAt(request.getCreatedAt());
//
//        return mapToResponse(expenseRepository.save(expense));
//    }
//
//    public void deleteExpense(Long id) {
//        expenseRepository.deleteById(id);
//    }

    private ExpenseResponse mapToResponse(Expense e) {
        ExpenseResponse res = new ExpenseResponse();
        res.setId(e.getId());
        res.setTitle(e.getTitle());
        res.setAmount(e.getAmount());
        res.setCategory(e.getCategory());
        res.setCreatedAt(e.getCreatedAt());
        return res;
    }
}
