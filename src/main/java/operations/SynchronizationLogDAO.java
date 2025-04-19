package operations;

import model.SynchronizationLog;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface SynchronizationLogDAO extends BaseDAO<SynchronizationLog, Long> {
    Optional<LocalDateTime> findLastSynchronizationTime() throws SQLException;
}