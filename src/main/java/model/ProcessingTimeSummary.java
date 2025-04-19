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
public class ProcessingTimeSummary {
    private Long id;
    private Long dishId;
    private Long salesPointId;
    private BigDecimal averageProcessingTime;
    private BigDecimal minimumProcessingTime;
    private BigDecimal maximumProcessingTime;
    private LocalDateTime lastUpdated;
}

