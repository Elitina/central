package service;

import dto.DishDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class DishIntegrationService {

    private final RestTemplate restTemplate;
    private final String posApiUrl;

    @Autowired
    public DishIntegrationService(RestTemplate restTemplate, @Value("${pos.api.url}") String posApiUrl) {
        this.restTemplate = restTemplate;
        this.posApiUrl = posApiUrl;
    }

    public List<DishDTO> fetchDishesFromPOS() {
        try {
            ResponseEntity<DishDTO[]> response = restTemplate.getForEntity(
                    posApiUrl + "/dishes", DishDTO[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                throw new RuntimeException("Échec de la récupération des plats depuis le POS.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la communication avec l'API POS pour les plats: " + e.getMessage(), e);
        }
    }
}