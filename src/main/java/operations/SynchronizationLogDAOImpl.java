package operations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

import model.SynchronizationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SynchronizationLogDAOImpl implements SynchronizationLogDAO {

    private final DataSource dataSource;

    @Autowired
    public SynchronizationLogDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<SynchronizationLog> findById(Long id) throws SQLException {
        String sql = "SELECT id, synchronizedAt FROM SynchronizationLog WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    SynchronizationLog log = SynchronizationLog.builder()
                            .id(rs.getLong("id"))
                            .synchronizedAt(rs.getTimestamp("synchronizedAt").toLocalDateTime())
                            .build();

                    return Optional.of(log);
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<SynchronizationLog> findAll() throws SQLException {
        String sql = "SELECT id, synchronizedAt FROM SynchronizationLog";
        List<SynchronizationLog> logs = new ArrayList<>();

        try (Connection conn = getConnection(dataSource);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SynchronizationLog log = SynchronizationLog.builder()
                        .id(rs.getLong("id"))
                        .synchronizedAt(rs.getTimestamp("synchronizedAt").toLocalDateTime())
                        .build();

                logs.add(log);
            }
        }

        return logs;
    }

    @Override
    public SynchronizationLog save(SynchronizationLog entity) throws SQLException {
        String sql = "INSERT INTO SynchronizationLog (synchronizedAt) VALUES (?) RETURNING id";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(entity.getSynchronizedAt()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getLong("id"));
                }
                return entity;
            }
        }
    }

    @Override
    public boolean update(SynchronizationLog entity) throws SQLException {
        String sql = "UPDATE SynchronizationLog SET synchronizedAt = ? WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(entity.getSynchronizedAt()));
            stmt.setLong(2, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM SynchronizationLog WHERE id = ?";

        try (Connection conn = getConnection(dataSource);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public Optional<LocalDateTime> findLastSynchronizationTime() throws SQLException {
        String sql = "SELECT MAX(synchronizedAt) as lastSync FROM SynchronizationLog";

        try (Connection conn = getConnection(dataSource);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("lastSync");
                if (timestamp != null) {
                    return Optional.of(timestamp.toLocalDateTime());
                }
            }
            return Optional.empty();
        }
    }
}

