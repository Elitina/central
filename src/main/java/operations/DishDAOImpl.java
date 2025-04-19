package operations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import model.Dish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DishDAOImpl implements DishDAO {

    private final DataSource dataSource;
    private final SalesPointDAO salesPointDAO;

    @Autowired
    public DishDAOImpl(DataSource dataSource, SalesPointDAO salesPointDAO) {
        this.dataSource = dataSource;
        this.salesPointDAO = salesPointDAO;
    }

    @Override
    public Optional<Dish> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, salesPointId FROM Dish WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Dish dish = Dish.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .salesPointId(rs.getLong("salesPointId"))
                            .build();

                    // Optionally load the SalesPoint
                    Long salesPointId = rs.getLong("salesPointId");
                    salesPointDAO.findById(salesPointId).ifPresent(dish::setSalesPoint);

                    return Optional.of(dish);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<Dish> findAll() throws SQLException {
        String sql = "SELECT id, name, salesPointId FROM Dish";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Dish dish = Dish.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .salesPointId(rs.getLong("salesPointId"))
                        .build();

                dishes.add(dish);
            }
        }

        return dishes;
    }

    @Override
    public Dish save(Dish entity) throws SQLException {
        String sql = "INSERT INTO Dish (name, salesPointId) VALUES (?, ?) RETURNING id";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setLong(2, entity.getSalesPointId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getLong("id"));
                }
                return entity;
            }
        }
    }

    @Override
    public boolean update(Dish entity) throws SQLException {
        String sql = "UPDATE Dish SET name = ?, salesPointId = ? WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setLong(2, entity.getSalesPointId());
            stmt.setLong(3, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM Dish WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public List<Dish> findBySalesPointId(Long salesPointId) throws SQLException {
        String sql = "SELECT id, name, salesPointId FROM Dish WHERE salesPointId = ?";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, salesPointId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = Dish.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .salesPointId(rs.getLong("salesPointId"))
                            .build();

                    dishes.add(dish);
                }
            }
        }

        return dishes;
    }

    @Override
    public List<Dish> findByNameContainingAndSalesPointId(String name, Long salesPointId) throws SQLException {
        String sql = "SELECT id, name, salesPointId FROM Dish WHERE name LIKE ? AND salesPointId = ?";
        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + name + "%");
            stmt.setLong(2, salesPointId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Dish dish = Dish.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .salesPointId(rs.getLong("salesPointId"))
                            .build();

                    dishes.add(dish);
                }
            }
        }

        return dishes;
    }
}