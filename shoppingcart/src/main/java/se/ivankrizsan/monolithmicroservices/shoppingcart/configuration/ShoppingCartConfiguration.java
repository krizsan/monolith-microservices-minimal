package se.ivankrizsan.monolithmicroservices.shoppingcart.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import se.ivankrizsan.monolithmicroservices.shoppingcart.api.ShoppingCartService;
import se.ivankrizsan.monolithmicroservices.shoppingcart.domain.ShoppingCartItem;
import se.ivankrizsan.monolithmicroservices.shoppingcart.implementation.ShoppingCartServiceImplementation;
import se.ivankrizsan.monolithmicroservices.shoppingcart.persistence.ShoppingCartItemRepository;
import se.ivankrizsan.monolithmicroservices.warehouse.api.WarehouseService;

/**
 * Configuration that creates the necessary beans needed for the shoppingcart service.
 *
 * @author Ivan Krizsan
 */
@Configuration
@EntityScan(basePackageClasses = ShoppingCartItem.class)
@EnableJpaRepositories(basePackageClasses = ShoppingCartItemRepository.class)
public class ShoppingCartConfiguration {

    /**
     * Creates a shoppingcart service for shopping products in the warehouse represented by the
     * supplied warehouse service.
     *
     * @param inWarehouseService Warehouse service representing warehouse with products.
     * @return Shoppingcart service.
     */
    @Bean
    protected ShoppingCartService shoppingCartService(final WarehouseService inWarehouseService) {
        return new ShoppingCartServiceImplementation(inWarehouseService);
    }
}
