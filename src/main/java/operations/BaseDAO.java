package operations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public interface BaseDAO<T, ID> {
    Optional<T> findById(ID id) throws SQLException;
    List<T> findAll() throws SQLException;
    T save(T entity) throws SQLException;
    boolean update(T entity) throws SQLException;
    boolean deleteById(ID id) throws SQLException;


    default Connection getConnection(DataSource dataSource) throws SQLException {
        return dataSource.getConnection();
    }
}