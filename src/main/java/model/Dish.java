package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish {
    private Long id;
    private String name;
    private Long salesPointId;


    public void setSalesPoint(SalesPoint salesPoint) {
    }
}