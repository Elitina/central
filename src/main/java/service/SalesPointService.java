package service;

import model.SalesPoint;
import operations.SalesPointDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class SalesPointService {

    private final SalesPointDAO salesPointDAO;
    private static final Long DEFAULT_SALES_POINT_ID = 1L; // ID du point de vente par défaut

    @Autowired
    public SalesPointService(SalesPointDAO salesPointDAO) {
        this.salesPointDAO = salesPointDAO;
    }

    /**
     * Récupère l'ID du point de vente par défaut.
     * Si aucun point de vente n'existe, en crée un.
     */
    public Long getDefaultSalesPointId() throws SQLException {
        // Essayer de récupérer le point de vente par défaut
        Optional<SalesPoint> defaultSalesPoint = salesPointDAO.findById(DEFAULT_SALES_POINT_ID);

        // Si le point de vente par défaut existe, retourner son ID
        if (defaultSalesPoint.isPresent()) {
            return defaultSalesPoint.get().getId();
        }

        // Sinon, vérifier s'il y a d'autres points de vente
        List<SalesPoint> salesPoints = salesPointDAO.findAll();
        if (!salesPoints.isEmpty()) {
            return salesPoints.get(0).getId(); // Retourner l'ID du premier point de vente trouvé
        }

        // Si aucun point de vente n'existe, en créer un nouveau
        SalesPoint newSalesPoint = SalesPoint.builder()
                .name("Antanimena")
                .build();

        SalesPoint savedSalesPoint = salesPointDAO.save(newSalesPoint);
        return savedSalesPoint.getId();
    }

    /**
     * Récupère tous les points de vente.
     */
    public List<SalesPoint> getAllSalesPoints() throws SQLException {
        return salesPointDAO.findAll();
    }

    /**
     * Récupère un point de vente par son ID.
     */
    public Optional<SalesPoint> getSalesPointById(Long id) throws SQLException {
        return salesPointDAO.findById(id);
    }

    /**
     * Crée un nouveau point de vente.
     */
    public SalesPoint createSalesPoint(SalesPoint salesPoint) throws SQLException {
        return salesPointDAO.save(salesPoint);
    }

    /**
     * Met à jour un point de vente existant.
     */
    public boolean updateSalesPoint(SalesPoint salesPoint) throws SQLException {
        return salesPointDAO.update(salesPoint);
    }

    /**
     * Supprime un point de vente par son ID.
     */
    public boolean deleteSalesPoint(Long id) throws SQLException {
        return salesPointDAO.deleteById(id);
    }

    /**
     * Recherche des points de vente par nom.
     */
    public List<SalesPoint> searchSalesPointsByName(String name) throws SQLException {
        return salesPointDAO.findByNameContaining(name);
    }
}