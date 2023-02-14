package se.ivankrizsan.monolithmicroservices.warehouse.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * Entity representing a reserved amount of a product in the warehouse.
 *
 * @author Ivan Krizsan
 */
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ProductReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "product_number", nullable = false)
    protected String productNumber;
    @Column(name = "reserved_amount", nullable = false)
    protected Double reservedAmount;

    /**
     * Creates a product reservation for the product with the supplied product number and the supplied amount.
     *
     * @param inProductNumber Product number which to create product reservation for.
     * @param inReservedAmount Amount of product reserved.
     */
    public ProductReservation(final String inProductNumber, final Double inReservedAmount) {
        productNumber = inProductNumber;
        reservedAmount = inReservedAmount;
    }

    @Override
    public boolean equals(final Object inOtherObject) {
        if (this == inOtherObject) return true;
        if (inOtherObject == null || getClass() != inOtherObject.getClass()) return false;
        ProductReservation theProductReservation = (ProductReservation) inOtherObject;
        return productNumber.equals(theProductReservation.productNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productNumber);
    }
}
