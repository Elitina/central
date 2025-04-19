package operations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import model.SalesPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SalesPointDAOImpl implements SalesPointDAO {

    private final DataSource dataSource;

    @Autowired
    public SalesPointDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<SalesPoint> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, createdAt, updatedAt FROM SalesPoint WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalesPoint salesPoint = SalesPoint.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updatedAt").toLocalDateTime())
                            .build();

                    return Optional.of(salesPoint);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<SalesPoint> findAll() throws SQLException {
        String sql = "SELECT id, name, createdAt, updatedAt FROM SalesPoint";
        List<SalesPoint> salesPoints = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SalesPoint salesPoint = SalesPoint.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                        .updatedAt(rs.getTimestamp("updatedAt").toLocalDateTime())
                        .build();

                salesPoints.add(salesPoint);
            }
        }

        return salesPoints;
    }

    @Override
    public SalesPoint save(SalesPoint entity) throws SQLException {
        String sql = "INSERT INTO SalesPoint (name) VALUES (?) RETURNING id, createdAt, updatedAt";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getLong("id"));
                    entity.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                    entity.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                }
                return entity;
            }
        }
    }

    @Override
    public boolean update(SalesPoint entity) throws SQLException {
        String sql = "UPDATE SalesPoint SET name = ?, updatedAt = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setLong(2, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM SalesPoint WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public List<SalesPoint> findByNameContaining(String name) throws SQLException {
        String sql = "SELECT id, name, createdAt, updatedAt FROM SalesPoint WHERE name LIKE ?";
        List<SalesPoint> salesPoints = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesPoint salesPoint = SalesPoint.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                            .updatedAt(rs.getTimestamp("updatedAt").toLocalDateTime())
                            .build();

                    salesPoints.add(salesPoint);
                }
            }
        }

        return salesPoints;
    }
}
