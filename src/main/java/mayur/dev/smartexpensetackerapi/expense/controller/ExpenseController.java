package mayur.dev.smartexpensetackerapi.expense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.ai.dto.InsightResponse;
import mayur.dev.smartexpensetackerapi.core.utils.SecurityUtils;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.expense.dto.ExpenseRequest;
import mayur.dev.smartexpensetackerapi.expense.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.expense.service.ExpenseService;
import mayur.dev.smartexpensetackerapi.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // 1. Added @ResponseStatus(HttpStatus.CREATED)
    // 2. Added @Valid to trigger validation rules
    // 3. Switched to Request/Response DTOs
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseResponse data = expenseService.createExpense(expenseRequest);
        return ResponseEntity.ok(ApiResponse.success(data, "Expense created " + "successfully!"));

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(@RequestParam(required = false) String category) {
        List<ExpenseResponse> data;
        User user = SecurityUtils.getCurrentUser();
        if (category != null && !category.isBlank()) {
            String categoryInLowerCase=category.toLowerCase();
            data =
                    expenseService.getExpensesByCategory(user.getId(), categoryInLowerCase);
        } else {
            data = expenseService.getAllExpenses();
        }

        return ResponseEntity.ok(ApiResponse.success(data, "Expenses fetched successfully!"));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotalExpense() {
        var totalExpense = expenseService.getTotalExpense();
        return ResponseEntity.ok(ApiResponse.success(totalExpense, "Retrieved " + totalExpense + " total expense"));
    }

    @GetMapping("/analytics/category")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> categoryAnalytics() {
        var categoryAnalytic = expenseService.getCategoryAnalytics();
        return ResponseEntity.ok(ApiResponse.success(categoryAnalytic, "Retrieved " + categoryAnalytic));
    }



    @GetMapping("/insights")
    public InsightResponse getInsights(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {

        LocalDate now = LocalDate.now();

        int finalMonth = (month != null) ? month : now.getMonthValue();
        int finalYear = (year != null) ? year : now.getYear();
        User user = SecurityUtils.getCurrentUser(); // from security context
        return expenseService.getMonthlyInsights(user.getId(), month, year);
    }


}
