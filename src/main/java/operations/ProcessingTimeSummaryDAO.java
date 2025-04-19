package operations;

import model.ProcessingTimeSummary;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProcessingTimeSummaryDAO extends BaseDAO<ProcessingTimeSummary, Long> {
    List<ProcessingTimeSummary> findBySalesPointId(Long salesPointId) throws SQLException;
    List<ProcessingTimeSummary> findBestProcessingTimeBySalesPointId(Long salesPointId) throws SQLException;
    Optional<ProcessingTimeSummary> findByDishIdAndSalesPointId(Long dishId, Long salesPointId) throws SQLException;
}

