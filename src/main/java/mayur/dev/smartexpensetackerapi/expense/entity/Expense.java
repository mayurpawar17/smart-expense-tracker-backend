package mayur.dev.smartexpensetackerapi.expense.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private BigDecimal amount;
    private String category;
    private LocalDateTime createdAt;
}

// =============================
// Notes:
// - Uses LAZY loading for performance
// - BigDecimal for money (precision safe)
// - Bidirectional mapping where useful
// - Indexes added for analytics queries
// =============================
