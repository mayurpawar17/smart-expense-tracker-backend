package mayur.dev.smartexpensetackerapi.features.expense.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.core.utils.security.SecurityUtils;
import mayur.dev.smartexpensetackerapi.core.utils.dto.ApiResponse;
import mayur.dev.smartexpensetackerapi.core.utils.dto.Pagination;
import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponse;
import mayur.dev.smartexpensetackerapi.features.category.entity.CategoryData;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseRequest;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.features.expense.service.ExpenseService;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // 1. Added @ResponseStatus(HttpStatus.CREATED)
    // 2. Added @Valid to trigger validation rules
    // 3. Switched to Request/Response DTOs
    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(@RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseResponse data = expenseService.createExpense(expenseRequest);
        ApiResponse<ExpenseResponse> body = ApiResponse.success("Expense created successfully!", data);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String category) {
        User user = SecurityUtils.getCurrentUser();

        Page<ExpenseResponse> expensePage = expenseService.getExpenses(user.getId(), category, page, size);

        Pagination<ExpenseResponse> pagination = new Pagination<>();
        pagination.setPage(expensePage.getNumber());
        pagination.setSize(expensePage.getSize());
        pagination.setTotalElements(expensePage.getTotalElements());
        pagination.setTotalPages(expensePage.getTotalPages());
        pagination.setLast(expensePage.isLast());

        var data = expensePage.getContent();
        ApiResponse<List<ExpenseResponse>> body = ApiResponse.success("Expenses fetched successfully!", data, pagination);
        return ResponseEntity.status(HttpStatus.OK).body(body);

    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotalExpense() {
        User user = SecurityUtils.getCurrentUser();
        double data = expenseService.getTotalExpense(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Retrieved " + data + " total expense", data));
    }

    @GetMapping("/analytics/category")
    public ResponseEntity<ApiResponse<List<CategoryData>>> categoryAnalytics() {
        User user = SecurityUtils.getCurrentUser();
        var data = expenseService.getCategoryAnalytics(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Retrieved Category expense successfully", data));
    }

    @GetMapping("/current-month-total")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getCurrentMonthTotal() {
        User user = SecurityUtils.getCurrentUser();
        // 1. Get the single value from the service
        BigDecimal total = expenseService.getCurrentMonthTotal(user.getId());

        // 2. Create a Map and put the total inside it
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("totalExpense", total);

        // 3. Return the Map inside your ApiResponse
        return ResponseEntity.ok(ApiResponse.success("Retrieved total expense successfully", data));
    }


    @GetMapping("/insights")
    public InsightResponse getInsights(@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {

        LocalDate now = LocalDate.now();

        int finalMonth = (month != null) ? month : now.getMonthValue();
        int finalYear = (year != null) ? year : now.getYear();
        User user = SecurityUtils.getCurrentUser(); // from security context
        return expenseService.getMonthlyInsights(user.getId(), month, year);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@PathVariable Long id, @RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseResponse data = expenseService.updateExpense(id, expenseRequest);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully!", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        var data = " Id: " + id + " Expense deleted successfully!";
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully!", data));
    }


}
