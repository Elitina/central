package dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleSummaryDTO {
    private String salesPoint;
    private String dish;
    private int quantitySold;
    private BigDecimal totalAmount;
}