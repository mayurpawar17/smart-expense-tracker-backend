package mayur.dev.smartexpensetackerapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.dto.ExpenseRequest;
import mayur.dev.smartexpensetackerapi.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ApiResponse<ExpenseResponse> createExpense(@RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseResponse data = expenseService.createExpense(expenseRequest);
        return ApiResponse.success(data, "Expense created successfully!");

    }

    @GetMapping
    public ApiResponse<List<ExpenseResponse>> getAllExpenses(@RequestParam(required = false) String category) {
        List<ExpenseResponse> data ;
//        return ApiResponse.success(data, "Retrieved " + data.size() + " expenses");

        if (category != null && !category.isBlank()) {
            data = expenseService.getExpensesByCategory(category);
        } else {
            data = expenseService.getAllExpenses();
        }

        return ApiResponse.success(data, "Expenses fetched successfully!");
    }

    @GetMapping("/total")
    public ApiResponse<Double> getTotalExpense() {
        var totalExpense = expenseService.getTotalExpense();
        return ApiResponse.success(totalExpense, "Retrieved " + totalExpense + " total expense");
    }

    @GetMapping("/analytics/category")
    public ApiResponse<Map<String, BigDecimal>> categoryAnalytics() {
        var categoryAnalytic = expenseService.getCategoryAnalytics();
        return ApiResponse.success(categoryAnalytic, "Retrieved " + categoryAnalytic);
    }

//    @GetMapping
//    public ApiResponse<List<ExpenseResponse>> getExpensesByCategory() {
//        List<ExpenseResponse> data = expenseService.getExpensesByCategory();
//        return ApiResponse.success(data, "Retrieved " + data.size() + " expenses");
//    }




}
