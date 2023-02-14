package se.ivankrizsan.monolithmicroservices.warehouse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ivankrizsan.monolithmicroservices.warehouse.domain.ProductReservation;

import java.util.List;

/**
 * Repository containing reservations of products in a warehouse.
 *
 * @author Ivan Krizsan
 */
@Repository
public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long> {
    /**
     * Finds the product reservations for the product with the supplied product number.
     *
     * @param inProductNumber Product number of product reservations to find.
     * @return Product reservations for the product, or empty list.
     */
    List<ProductReservation> findAllByProductNumber(String inProductNumber);
}
