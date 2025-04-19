package operations;

import model.SalesPoint;

import java.sql.SQLException;
import java.util.List;


public interface SalesPointDAO extends BaseDAO<SalesPoint, Long> {
    List<SalesPoint> findByNameContaining(String name) throws SQLException;
}
