package se.ivankrizsan.monolithmicroservices.modules.warehouse.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * Entity representing a product in a warehouse.
 *
 * @author Ivan Krizsan
 */
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(fluent = true)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "product_number", nullable = false, unique = true)
    @NonNull
    protected String productNumber;
    @Column(nullable = false)
    @NonNull
    protected String name;
    @Column(name = "available_amount")
    protected double availableAmount;
    @Column(name = "reserved_amount")
    protected double reservedAmount;
    @Column(name = "unit_price")
    protected double unitPrice;

    /**
     * Creates a product having the supplied product number and the supplied name with zero
     * available and reserved amounts.
     *
     * @param inProductNumber Product number of product.
     * @param inProductName Name of product.
     */
    public Product(final String inProductNumber, final String inProductName) {
        productNumber = inProductNumber;
        name = inProductName;
        availableAmount = 0;
        reservedAmount = 0;
    }

    @Override
    public boolean equals(final Object inOtherObject) {
        if (this == inOtherObject) return true;
        if (inOtherObject == null || getClass() != inOtherObject.getClass()) return false;
        Product theOtherProduct = (Product) inOtherObject;
        return productNumber.equals(theOtherProduct.productNumber) && name.equals(theOtherProduct.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productNumber, name);
    }
}
