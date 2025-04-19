package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSummary {
    private Long id;
    private Long dishId;
    private Long salesPointId;
    private Integer quantitySold;
    private BigDecimal totalAmount;
    private LocalDateTime lastUpdated;
}
