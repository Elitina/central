package service;

import dto.DishDTO;
import dto.SaleDTO;
import dto.SaleSummaryDTO;
import dto.SynchronizationResponse;
import model.Dish;
import model.SalesPoint;
import model.SalesSummary;
import model.SynchronizationLog;
import operations.SynchronizationLogDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import service.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SynchronizationService {

    private final SynchronizationLogDAO synchronizationLogDAO;
    private final SaleIntegrationService saleIntegrationService;
    private final DishIntegrationService dishIntegrationService;
    private final SalesSummaryService salesSummaryService;
    private final SalesPointService salesPointService;
    private final DishService dishService;

    @Autowired
    public SynchronizationService(SynchronizationLogDAO synchronizationLogDAO,
                                  SaleIntegrationService saleIntegrationService,
                                  DishIntegrationService dishIntegrationService,
                                  SalesSummaryService salesSummaryService,
                                  SalesPointService salesPointService,
                                  DishService dishService) {
        this.synchronizationLogDAO = synchronizationLogDAO;
        this.saleIntegrationService = saleIntegrationService;
        this.dishIntegrationService = dishIntegrationService;
        this.salesSummaryService = salesSummaryService;
        this.salesPointService = salesPointService;
        this.dishService = dishService;
    }

    /**
     * Lance la synchronisation complète.
     * 1. Récupère les ventes et les plats via l'API POS.
     * 2. Met à jour les tables Dish, SalesPoint et SalesSummary.
     * 3. Journalise l'heure de synchro.
     */
    public SynchronizationResponse synchronizeData() throws SQLException {
        // Récupérer les données nécessaires
        List<SaleDTO> salesFromPOS = saleIntegrationService.fetchSalesFromPOS();
        List<DishDTO> dishesFromPOS = dishIntegrationService.fetchDishesFromPOS();

        // Obtenir le point de vente par défaut
        Long defaultSalesPointId = salesPointService.getDefaultSalesPointId();
        Optional<SalesPoint> salesPoint = salesPointService.getSalesPointById(defaultSalesPointId);

        // Créer une map des plats pour un accès rapide par ID
        Map<Long, DishDTO> dishMap = dishesFromPOS.stream()
                .collect(Collectors.toMap(DishDTO::getId, Function.identity()));

        List<SaleSummaryDTO> summaries = new ArrayList<>();

        // Traiter chaque vente
        for (SaleDTO sale : salesFromPOS) {
            // Récupérer le plat correspondant à la vente
            DishDTO dishDTO = dishMap.get(sale.getDishIdentifier());

            if (dishDTO != null) {
                // Créer ou récupérer le plat dans la base de données locale
                Dish dish = dishService.getOrCreateDish(dishDTO.getName(), defaultSalesPointId);

                // Calculer le montant total (quantité vendue * prix du plat)
                BigDecimal price = dishDTO.getActualPrice() != null ?
                        dishDTO.getActualPrice() : BigDecimal.ZERO;

                BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(sale.getQuantitySold()));

                // Vérifier si un résumé existe déjà pour ce plat et ce point de vente
                Optional<SalesSummary> existingSummary = salesSummaryService
                        .getSalesSummaryByDishAndSalesPoint(dish.getId(), defaultSalesPointId);

                SalesSummary summary;
                if (existingSummary.isPresent()) {
                    summary = existingSummary.get();
                    summary.setQuantitySold(sale.getQuantitySold());
                    summary.setTotalAmount(totalAmount);
                    summary.setLastUpdated(LocalDateTime.now());
                } else {
                    summary = SalesSummary.builder()
                            .dishId(dish.getId())
                            .salesPointId(defaultSalesPointId)
                            .quantitySold(sale.getQuantitySold())
                            .totalAmount(totalAmount)
                            .lastUpdated(LocalDateTime.now())
                            .build();
                }

                // Sauvegarder ou mettre à jour le résumé
                salesSummaryService.saveOrUpdateSalesSummary(summary);

                // Ajouter au résultat de synchronisation
                summaries.add(SaleSummaryDTO.builder()
                        .salesPoint(salesPoint != null ? String.valueOf(salesPoint.get()) : "Antanimena")
                        .dish(dish.getName())
                        .quantitySold(sale.getQuantitySold())
                        .totalAmount(totalAmount)
                        .build());
            }
        }

        // Journalisation
        SynchronizationLog log = SynchronizationLog.builder()
                .synchronizedAt(LocalDateTime.now())
                .build();

        synchronizationLogDAO.save(log);

        // Construire et retourner la réponse
        return SynchronizationResponse.builder()
                .updatedAt(log.getSynchronizedAt())
                .sales(summaries)
                .build();
    }

    /**
     * Récupère la dernière date de synchronisation.
     */
    public LocalDateTime getLastSynchronizationTime() throws SQLException {
        return synchronizationLogDAO.findLastSynchronizationTime()
                .orElseThrow(() -> new IllegalStateException("Aucune synchronisation n'a encore été effectuée."));
    }
}



