package service;

import model.SalesSummary;
import operations.SalesSummaryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class SalesSummaryService {

    private final SalesSummaryDAO salesSummaryDAO;
    private final DishService dishService;
    private final SalesPointService salesPointService;

    @Autowired
    public SalesSummaryService(SalesSummaryDAO salesSummaryDAO,
                               DishService dishService,
                               SalesPointService salesPointService) {
        this.salesSummaryDAO = salesSummaryDAO;
        this.dishService = dishService;
        this.salesPointService = salesPointService;
    }

    /**
     * Sauvegarde ou met à jour un résumé de ventes.
     */
    public void saveOrUpdateSalesSummary(SalesSummary salesSummary) throws SQLException {
        salesSummaryDAO.saveOrUpdate(salesSummary);
    }

    /**
     * Récupère tous les résumés de ventes.
     */
    public List<SalesSummary> getAllSalesSummaries() throws SQLException {
        return salesSummaryDAO.findAll();
    }

    /**
     * Récupère les meilleures ventes.
     */
    public List<SalesSummary> getBestSales(int limit) throws SQLException {
        return salesSummaryDAO.findBestSales(limit);
    }

    /**
     * Récupère un résumé de ventes par ID de plat et ID de point de vente.
     */
    public Optional<SalesSummary> getSalesSummaryByDishAndSalesPoint(Long dishId, Long salesPointId) throws SQLException {
        return salesSummaryDAO.findByDishIdAndSalesPointId(dishId, salesPointId);
    }

     }