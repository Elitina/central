package operations;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import model.SalesSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SalesSummaryDAOImpl implements SalesSummaryDAO {

    private final DataSource dataSource;

    @Autowired
    public SalesSummaryDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<SalesSummary> findById(Long id) throws SQLException {
        String sql = "SELECT id, dishId, salesPointId, quantitySold, totalAmount, lastUpdated " +
                "FROM SalesSummary WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalesSummary summary = buildSalesSummaryFromResultSet(rs);
                    return Optional.of(summary);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<SalesSummary> findAll() throws SQLException {
        String sql = "SELECT id, dishId, salesPointId, quantitySold, totalAmount, lastUpdated FROM SalesSummary";
        List<SalesSummary> salesSummaries = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                salesSummaries.add(buildSalesSummaryFromResultSet(rs));
            }
        }

        return salesSummaries;
    }

    @Override
    public SalesSummary save(SalesSummary entity) throws SQLException {
        String sql = "INSERT INTO SalesSummary (dishId, salesPointId, quantitySold, totalAmount, lastUpdated) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, entity.getDishId());
            stmt.setLong(2, entity.getSalesPointId());
            stmt.setInt(3, entity.getQuantitySold());
            stmt.setBigDecimal(4, entity.getTotalAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(entity.getLastUpdated()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getLong("id"));
                }
                return entity;
            }
        }
    }

    @Override
    public boolean update(SalesSummary entity) throws SQLException {
        String sql = "UPDATE SalesSummary SET quantitySold = ?, totalAmount = ?, lastUpdated = ? " +
                "WHERE dishId = ? AND salesPointId = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getQuantitySold());
            stmt.setBigDecimal(2, entity.getTotalAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getLastUpdated()));
            stmt.setLong(4, entity.getDishId());
            stmt.setLong(5, entity.getSalesPointId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM SalesSummary WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }


    @Override
    public Optional<SalesSummary> findByDishIdAndSalesPointId(Long dishId, Long salesPointId) throws SQLException {
        String sql = "SELECT id, dishId, salesPointId, quantitySold, totalAmount, lastUpdated " +
                "FROM SalesSummary WHERE dishId = ? AND salesPointId = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, dishId);
            stmt.setLong(2, salesPointId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SalesSummary summary = buildSalesSummaryFromResultSet(rs);
                    return Optional.of(summary);
                }
                return Optional.empty();
            }
        }
    }

    private SalesSummary buildSalesSummaryFromResultSet(ResultSet rs) throws SQLException {
        SalesSummary summary = SalesSummary.builder()
                .id(rs.getLong("id"))
                .dishId(rs.getLong("dishId"))
                .salesPointId(rs.getLong("salesPointId"))
                .quantitySold(rs.getInt("quantitySold"))
                .totalAmount(rs.getBigDecimal("totalAmount"))
                .lastUpdated(rs.getTimestamp("lastUpdated").toLocalDateTime())
                .build();
        return summary;
    }

    @Override
    public List<SalesSummary> findBestSales(int limit) throws SQLException {
        String sql = "SELECT id, dishId, salesPointId, quantitySold, totalAmount, lastUpdated " +
                "FROM SalesSummary ORDER BY quantitySold DESC LIMIT ?";
        List<SalesSummary> bestSales = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesSummary summary = new SalesSummary();
                    summary.setId(rs.getLong("id"));
                    summary.setDishId(rs.getLong("dishId"));
                    summary.setSalesPointId(rs.getLong("salesPointId"));
                    summary.setQuantitySold(rs.getInt("quantitySold"));
                    summary.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    summary.setLastUpdated(rs.getTimestamp("lastUpdated").toLocalDateTime());

                    bestSales.add(summary);
                }
            }
        }

        return bestSales;
    }
    @Override
    public List<SalesSummary> findBestSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit) throws SQLException {
        String sql = "SELECT ss.id, ss.dishId, ss.salesPointId, ss.quantitySold, ss.totalAmount, ss.lastUpdated " +
                "FROM SalesSummary ss " +
                "JOIN Dish d ON ss.dishId = d.id " +
                "JOIN SalesPoint sp ON ss.salesPointId = sp.id " +
                "WHERE ss.lastUpdated BETWEEN ? AND ? " +
                "ORDER BY ss.quantitySold DESC " +
                "LIMIT ?";

        List<SalesSummary> bestSales = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SalesSummary summary = new SalesSummary();
                    summary.setId(rs.getLong("id"));
                    summary.setDishId(rs.getLong("dishId"));
                    summary.setSalesPointId(rs.getLong("salesPointId"));
                    summary.setQuantitySold(rs.getInt("quantitySold"));
                    summary.setTotalAmount(rs.getBigDecimal("totalAmount"));
                    summary.setLastUpdated(rs.getTimestamp("lastUpdated").toLocalDateTime());

                    bestSales.add(summary);
                }
            }
        }

        return bestSales;
    }

    @Override
    public void saveOrUpdate(SalesSummary salesSummary) throws SQLException {
        Optional<SalesSummary> existingSummary = findByDishIdAndSalesPointId(
                salesSummary.getDishId(), salesSummary.getSalesPointId());

        if (existingSummary.isPresent()) {
            // Mise à jour de l'enregistrement existant
            String sql = "UPDATE SalesSummary SET quantitySold = ?, totalAmount = ?, lastUpdated = CURRENT_TIMESTAMP " +
                    "WHERE dishId = ? AND salesPointId = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, salesSummary.getQuantitySold());
                stmt.setBigDecimal(2, salesSummary.getTotalAmount());
                stmt.setLong(3, salesSummary.getDishId());
                stmt.setLong(4, salesSummary.getSalesPointId());

                stmt.executeUpdate();

                // Mettre à jour l'ID
                salesSummary.setId(existingSummary.get().getId());
            }
        } else {
            // Insertion d'un nouvel enregistrement
            String sql = "INSERT INTO SalesSummary (dishId, salesPointId, quantitySold, totalAmount) " +
                    "VALUES (?, ?, ?, ?) RETURNING id, lastUpdated";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setLong(1, salesSummary.getDishId());
                stmt.setLong(2, salesSummary.getSalesPointId());
                stmt.setInt(3, salesSummary.getQuantitySold());
                stmt.setBigDecimal(4, salesSummary.getTotalAmount());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        salesSummary.setId(rs.getLong("id"));
                        salesSummary.setLastUpdated(rs.getTimestamp("lastUpdated").toLocalDateTime());
                    }
                }
            }
        }
    }
}