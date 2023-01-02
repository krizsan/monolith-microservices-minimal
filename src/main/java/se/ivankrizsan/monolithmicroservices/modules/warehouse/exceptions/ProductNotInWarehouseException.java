package se.ivankrizsan.monolithmicroservices.modules.warehouse.exceptions;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Exception that indicates that a product does not exist in the warehouse.
 *
 * @author Ivan Krizsan
 */
@NoArgsConstructor
@RequiredArgsConstructor
public class ProductNotInWarehouseException extends WarehouseException {
    /* Constant(s): */

    /* Instance variable(s): */
    /** Product number of the product that does not exist in the warehouse or null if no product number available. */
    @NonNull
    protected String productNumber;
}
