package se.ivankrizsan.monolithmicroservices.warehouse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ivankrizsan.monolithmicroservices.warehouse.domain.Product;

import java.util.Optional;

/**
 * Repository containing products in the warehouse.
 *
 * @author Ivan Krizsan
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     * Finds the product in the warehouse with the supplied product number.
     *
     * @param inProductNumber Product number of product to find.
     * @return Matching product or empty if no matching product found.
     */
    Optional<Product> findByProductNumber(String inProductNumber);

    /**
     * Determines whether a product with the supplied product number exists in the repository.
     *
     * @param inProductNumber Product number of product to check whether present in the repository.
     * @return True if a product with the product number exists in the repository, false otherwise.
     */
    boolean existsByProductNumber(String inProductNumber);
}
