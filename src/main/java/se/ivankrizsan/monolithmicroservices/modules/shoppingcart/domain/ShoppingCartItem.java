package se.ivankrizsan.monolithmicroservices.modules.shoppingcart.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an entry in a shopping cart.
 *
 * @author Ivan Krizsan
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class ShoppingCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "product_number", nullable = false)
    @NonNull
    protected String productNumber;
    @Column
    @NonNull
    protected double amount;

}
