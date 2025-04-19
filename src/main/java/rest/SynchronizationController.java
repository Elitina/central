package rest;

import dto.SynchronizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.SynchronizationService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SynchronizationController {

    private final SynchronizationService synchronizationService;

    @Autowired
    public SynchronizationController(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @PostMapping("/synchronization")
    public ResponseEntity<SynchronizationResponse> synchronize() {
        try {
            SynchronizationResponse response = synchronizationService.synchronizeData();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/synchronization/last")
    public ResponseEntity<Map<String, LocalDateTime>> getLastSynchronizationTime() {
        try {
            LocalDateTime lastSync = synchronizationService.getLastSynchronizationTime();
            Map<String, LocalDateTime> response = new HashMap<>();
            response.put("lastSynchronizedAt", lastSync);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}