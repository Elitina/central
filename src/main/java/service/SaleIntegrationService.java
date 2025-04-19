package service;

import dto.SaleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class SaleIntegrationService {

    private final RestTemplate restTemplate;
    private final String posApiUrl;

    @Autowired
    public SaleIntegrationService(RestTemplate restTemplate, @Value("${pos.api.url}") String posApiUrl) {
        this.restTemplate = restTemplate;
        this.posApiUrl = posApiUrl;
    }

    public List<SaleDTO> fetchSalesFromPOS() {
        try {
            ResponseEntity<SaleDTO[]> response = restTemplate.getForEntity(
                    posApiUrl + "/orders/sales", SaleDTO[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                throw new RuntimeException("Échec de la récupération des ventes depuis le POS.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la communication avec l'API POS pour les ventes: " + e.getMessage(), e);
        }
    }
}