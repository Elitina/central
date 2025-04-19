package operations;

import model.Dish;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DishDAO extends BaseDAO<Dish, Long> {
    List<Dish> findBySalesPointId(Long salesPointId) throws SQLException;
    List<Dish> findByNameContainingAndSalesPointId(String name, Long salesPointId) throws SQLException;
}
