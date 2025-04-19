package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishDTO {
    private Long id;
    private String name;
    private Integer availableQuantity;
    private BigDecimal actualPrice;
    private List<IngredientRequirementDTO> ingredients;
}
