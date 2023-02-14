package se.ivankrizsan.monolithmicroservices.shoppingcart.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ivankrizsan.monolithmicroservices.shoppingcart.domain.ShoppingCartItem;

/**
 * Repository containing items in a shopping cart.
 *
 * @author Ivan Krizsan
 */
@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, Long> {
}