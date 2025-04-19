package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientRequirementDTO {
    private Integer requiredQuantity;
    private String unit;
    private IngredientDTO ingredient;
}