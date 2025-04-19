package service;

import model.Dish;
import operations.DishDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class DishService {

    private final DishDAO dishDAO;

    @Autowired
    public DishService(DishDAO dishDAO) {
        this.dishDAO = dishDAO;
    }

    /**
     * Récupère tous les plats.
     */
    public List<Dish> getAllDishes() throws SQLException {
        return dishDAO.findAll();
    }

    /**
     * Récupère un plat par son ID.
     */
    public Optional<Dish> getDishById(Long id) throws SQLException {
        return dishDAO.findById(id);
    }

    /**
     * Crée un nouveau plat ou récupère un plat existant par son nom et son point de vente.
     */
    public Dish getOrCreateDish(String name, Long salesPointId) throws SQLException {
        List<Dish> existingDish = dishDAO.findByNameContainingAndSalesPointId(name, salesPointId);

        if (!existingDish.isEmpty()) {
            return existingDish.getFirst();
        } else {
            Dish newDish = Dish.builder()
                    .name(name)
                    .salesPointId(salesPointId)
                    .build();

            return dishDAO.save(newDish);
        }
    }

    /**
     * Met à jour un plat existant.
     */
    public boolean updateDish(Dish dish) throws SQLException {
        return dishDAO.update(dish);
    }

    /**
     * Supprime un plat par son ID.
     */
    public boolean deleteDish(Long id) throws SQLException {
        return dishDAO.deleteById(id);
    }

    /**
     * Récupère tous les plats d'un point de vente spécifique.
     */
    public List<Dish> getDishesBySalesPointId(Long salesPointId) throws SQLException {
        return dishDAO.findBySalesPointId(salesPointId);
    }
}