package mayur.dev.smartexpensetackerapi.features.expense.service;

import lombok.RequiredArgsConstructor;
import mayur.dev.smartexpensetackerapi.features.ai.dto.ExpenseAiResponse;
import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponse;
import mayur.dev.smartexpensetackerapi.features.ai.service.AiService;
import mayur.dev.smartexpensetackerapi.features.category.dto.CategorySummary;
import mayur.dev.smartexpensetackerapi.features.category.entity.CategoryData;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseRequest;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponse;
import mayur.dev.smartexpensetackerapi.features.expense.entity.Expense;
import mayur.dev.smartexpensetackerapi.features.expense.repository.ExpenseRepository;
import mayur.dev.smartexpensetackerapi.features.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final AiService aiService;

    ExpenseAiResponse aiResponse = null;


    public ExpenseResponse createExpense(ExpenseRequest request) {
        User user = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        //Call AI
        ExpenseAiResponse aiResponse = null;

        try {
            aiResponse = aiService.extractExpense(request.getTitle());
        } catch (Exception e) {
            // log this (important for debugging)
            System.out.println("AI failed: " + e.getMessage());
        }
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());

        // ✅ CATEGORY FALLBACK
        String category = resolveCategory(request, aiResponse);
        expense.setCategory(category.toLowerCase());

        // ✅ AMOUNT FALLBACK
        BigDecimal amount = resolveAmount(request, aiResponse);
        expense.setAmount(amount);

////        expense.setCategory(request.getCategory().toLowerCase());
//        expense.setCategory(aiResponse.getCategory().toLowerCase());
//
////        expense.setAmount(request.getAmount());
//        expense.setAmount(BigDecimal.valueOf(aiResponse.getAmount()));


        // Set this manually here so the database always has the correct time
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUser(user);

        Expense saved = expenseRepository.save(expense);

        return mapToResponse(saved);
    }

    // public List<ExpenseResponse> getAllExpenses() {
    //     User user = SecurityUtils.getCurrentUser();
    //     return expenseRepository.findByUserId(user.getId()).stream().map(this::mapToResponse).collect(Collectors.toList());
    // }

    //  public List<ExpenseResponse> getExpensesByCategory(Long userId, String category) {
    //     return expenseRepository.findByUserIdAndCategory(userId, category).stream().map(this::mapToResponse).collect(Collectors.toList());
    // }

    public Page<ExpenseResponse> getExpenses(
        Long userId,
        String category,
        int page,
        int size
) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<Expense> expensePage;

    if (category != null && !category.isBlank()) {
        expensePage = expenseRepository
                .findByUserIdAndCategoryIgnoreCase(userId, category, pageable);
    } else {
        expensePage = expenseRepository
                .findByUserId(userId, pageable);
    }

    return expensePage.map(this::mapToResponse);
}

    public Double getTotalExpense(   Long userId) {
        return expenseRepository.getTotalExpenseByUserId(userId);
    }

    public List<CategoryData> getCategoryAnalytics(Long userId) {
        List<Object[]> data = expenseRepository.getCategoryWiseExpenseByUserId(userId);

        return data.stream()
                .map(row -> new CategoryData(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }

    public BigDecimal getCurrentMonthTotal(Long userId) {
        BigDecimal total = expenseRepository.sumTotalExpenseForCurrentMonth(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

   


    public InsightResponse getMonthlyInsights(Long userId, int month, int year) {
        List<CategorySummary> summary =
                expenseRepository.getMonthlySummary(userId, month, year);
        return aiService.generateInsights(summary);
    }

   public ExpenseResponse updateExpense(Long id, ExpenseRequest request) {
       Expense expense = expenseRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Expense not found"));

       expense.setTitle(request.getTitle());
       expense.setAmount(request.getAmount());
       //default current time
       expense.setCreatedAt(LocalDateTime.now());
       return mapToResponse(expenseRepository.save(expense));
   }

   public void deleteExpense(Long id) {
       expenseRepository.deleteById(id);
   }

    private ExpenseResponse mapToResponse(Expense e) {
        ExpenseResponse res = new ExpenseResponse();
        res.setId(e.getId());
        res.setTitle(e.getTitle());
        res.setAmount(e.getAmount());
        res.setCategory(e.getCategory());
        res.setCreatedAt(e.getCreatedAt());
        return res;
    }

    private String resolveCategory(ExpenseRequest request, ExpenseAiResponse ai) {

        // 1️⃣ Priority: AI
        if (ai != null && ai.getCategory() != null && !ai.getCategory().isBlank()) {
            return ai.getCategory();
        }

        // 2️⃣ Fallback: Request (if user sends manually)
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            return request.getCategory();
        }

        // 3️⃣ Final fallback
        return "Other";
    }

    private BigDecimal resolveAmount(ExpenseRequest request, ExpenseAiResponse ai) {

        // 1️⃣ AI value
        if (ai != null && ai.getAmount() != null && ai.getAmount() > 0) {
            return BigDecimal.valueOf(ai.getAmount());
        }

        // 2️⃣ Request value
        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return request.getAmount();
        }

        // 3️⃣ Extract from text (last fallback)
        Double extracted = extractAmountFromText(request.getTitle());
        if (extracted != null) {
            return BigDecimal.valueOf(extracted);
        }

        // 4️⃣ अंतिम fallback (fail-safe)
        throw new RuntimeException("Amount is required");
    }

    private Double extractAmountFromText(String text) {
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        }
        return null;
    }
}
