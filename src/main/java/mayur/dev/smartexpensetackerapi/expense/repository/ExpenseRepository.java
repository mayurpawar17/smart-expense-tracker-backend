package mayur.dev.smartexpensetackerapi.expense.repository;

import mayur.dev.smartexpensetackerapi.category.dto.CategorySummary;
import mayur.dev.smartexpensetackerapi.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
/*
This code is a Spring Data JPA Repository, which acts as the data access layer for your application. Using an interface like this allows you to interact with your database using Java methods instead of writing manual SQL connection logic.
*/

/*
Extending JpaRepository<Expense, Long>
By extending this, you inherit a massive amount of "free" functionality without writing a single line of implementation code. You automatically get methods like:

.save() (Create/Update)

.findAll() (Read)

.deleteById() (Delete)

Pagination and Sorting capabilities.
 */
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserIdAndCategory(Long userId,String category);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.id = :userId")
    Double getTotalExpenseByUserId(Long userId);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.user.id =" +
            " :userId GROUP BY e.category")
    List<Object[]> getCategoryWiseExpenseByUserId(Long userId);

    List<Expense> findByUserId(Long userId);

    @Query("""
SELECT new mayur.dev.smartexpensetackerapi.category.dto.CategorySummary(e.category, SUM(e.amount))
FROM Expense e
WHERE e.user.id = :userId
AND MONTH(e.createdAt) = :month
AND YEAR(e.createdAt) = :year
GROUP BY e.category
""")
    List<CategorySummary> getMonthlySummary(Long userId, int month, int year);

    @Query("SELECT SUM(e.amount) FROM Expense e " +
            "WHERE e.user.id = :userId " +
            "AND YEAR(e.createdAt) = YEAR(CURRENT_DATE) " +
            "AND MONTH(e.createdAt) = MONTH(CURRENT_DATE)")
    BigDecimal sumTotalExpenseForCurrentMonth(@Param("userId") Long userId);

}
