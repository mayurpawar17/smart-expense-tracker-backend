package mayur.dev.smartexpensetackerapi.repository;

import mayur.dev.smartexpensetackerapi.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    List<Expense> getExpensesByCategory(String category);

    @Query("SELECT SUM(e.amount) FROM Expense e")
    Double getTotalExpense();

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> getCategoryWiseExpense();
}
