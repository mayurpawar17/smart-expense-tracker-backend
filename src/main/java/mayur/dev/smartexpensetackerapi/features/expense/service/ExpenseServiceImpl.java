package mayur.dev.smartexpensetackerapi.features.expense.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mayur.dev.smartexpensetackerapi.core.utils.mapper.MapperUtils;
import mayur.dev.smartexpensetackerapi.features.ai.dto.ExpenseAiResponseDTO;
import mayur.dev.smartexpensetackerapi.features.ai.dto.InsightResponseDTO;
import mayur.dev.smartexpensetackerapi.features.ai.service.AiService;
import mayur.dev.smartexpensetackerapi.features.category.dto.CategorySummaryRequestDTO;
import mayur.dev.smartexpensetackerapi.features.category.entity.CategoryData;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseRequestDTO;
import mayur.dev.smartexpensetackerapi.features.expense.dto.ExpenseResponseDTO;
import mayur.dev.smartexpensetackerapi.features.expense.entity.Expense;
import mayur.dev.smartexpensetackerapi.features.expense.repository.ExpenseRepository;
import mayur.dev.smartexpensetackerapi.features.auth.entity.User;

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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final AiService aiService;
    private final jakarta.persistence.EntityManager entityManager;
    private final MapperUtils mapperUtils;

    ExpenseAiResponseDTO aiResponse = null;


    @Override
    public ExpenseResponseDTO createExpense(ExpenseRequestDTO request) {
        User principal = (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        //Call AI
        ExpenseAiResponseDTO aiResponse = null;

        try {
            aiResponse = aiService.extractExpense(request.getTitle());
        } catch (Exception e) {
            log.error("AI failed: " + e.getMessage());
        }
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());

        //CATEGORY FALLBACK
        String category = resolveCategory(request, aiResponse);
        expense.setCategory(category.toLowerCase());

        //AMOUNT FALLBACK
        BigDecimal amount = resolveAmount(request, aiResponse);
        expense.setAmount(amount);

////        expense.setCategory(request.getCategory().toLowerCase());
//        expense.setCategory(aiResponse.getCategory().toLowerCase());
//
////        expense.setAmount(request.getAmount());
//        expense.setAmount(BigDecimal.valueOf(aiResponse.getAmount()));


        // Set this manually here so the database always has the correct time
        expense.setCreatedAt(LocalDateTime.now());

        // This stops Hibernate from running an extra SELECT query on the user table before saving.
        User userProxy = entityManager.getReference(User.class, principal.getId());
        expense.setUser(userProxy);

        // Logical check: If description is null, empty, or just blank spaces
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            log.info("Description empty for expense '{}'. Triggering Groq AI generation...", request.getTitle());

            String aiDescription = aiService.generateDescriptionFromTitle(request.getTitle());
            expense.setDescription(aiDescription);
        } else {
            // Use the user's explicit input description
            expense.setDescription(request.getDescription().trim());
        }

        Expense saved = expenseRepository.save(expense);
        return mapperUtils.mapToExpenseResponse(saved);
    }

    @Override
    public Page<ExpenseResponseDTO> getExpenses(Long userId, String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Expense> expensePage;

        if (category != null && !category.isBlank()) {
            expensePage = expenseRepository.findByUserIdAndCategoryIgnoreCase(userId, category, pageable);
        } else {
            expensePage = expenseRepository.findByUserId(userId, pageable);
        }

        return expensePage.map(mapperUtils::mapToExpenseResponse);
    }

    @Override
    public Double getTotalExpenses(Long userId) {
        return expenseRepository.getTotalExpenseByUserId(userId);
    }

    @Override
    public List<CategoryData> getCategoryAnalytics(Long userId) {
        List<Object[]> data = expenseRepository.getCategoryWiseExpenseByUserId(userId);

        return data.stream().map(row -> new CategoryData((String) row[0], (BigDecimal) row[1])).toList();
    }

    @Override
    public BigDecimal getCurrentMonthTotal(Long userId) {
        BigDecimal total = expenseRepository.sumTotalExpenseForCurrentMonth(userId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public InsightResponseDTO getMonthlyInsights(Long userId, int month, int year) {
        List<CategorySummaryRequestDTO> summary = expenseRepository.getMonthlySummary(userId, month, year);
        return aiService.generateInsights(summary);
    }

    @Override
    public ExpenseResponseDTO updateExpense(Long id, ExpenseRequestDTO request) {
        Expense expense = expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        //default current time
        expense.setCreatedAt(LocalDateTime.now());

        Expense updatedExpense = expenseRepository.save(expense);

        return mapperUtils.mapToExpenseResponse(updatedExpense);
    }

    @Override
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }


    private String resolveCategory(ExpenseRequestDTO request, ExpenseAiResponseDTO ai) {

        //1 Priority: AI
        if (ai != null && ai.getCategory() != null && !ai.getCategory().isBlank()) {
            return ai.getCategory();
        }

        //2 Fallback: Request (if user sends manually)
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            return request.getCategory();
        }

        //3 Final fallback
        return "Other";
    }

    private BigDecimal resolveAmount(ExpenseRequestDTO request, ExpenseAiResponseDTO ai) {

        //1 AI value
        if (ai != null && ai.getAmount() != null && ai.getAmount() > 0) {
            return BigDecimal.valueOf(ai.getAmount());
        }

        //2 Request value
        if (request.getAmount() != null && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return request.getAmount();
        }

        //3 Extract from text (last fallback)
        Double extracted = extractAmountFromText(request.getTitle());
        if (extracted != null) {
            return BigDecimal.valueOf(extracted);
        }

        //4 fallback (fail-safe)
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
