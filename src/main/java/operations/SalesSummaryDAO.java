package operations;

import model.SalesSummary;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalesSummaryDAO extends BaseDAO<SalesSummary, Long> {
    public List<SalesSummary> findBestSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate, int limit) throws SQLException;
    void saveOrUpdate(SalesSummary salesSummary) throws SQLException;
    public List<SalesSummary> findBestSales(int limit) throws SQLException;
    Optional<SalesSummary> findByDishIdAndSalesPointId(Long dishId, Long salesPointId) throws SQLException;
}
