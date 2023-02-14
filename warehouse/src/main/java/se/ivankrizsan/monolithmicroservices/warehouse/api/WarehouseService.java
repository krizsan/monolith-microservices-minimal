package se.ivankrizsan.monolithmicroservices.warehouse.api;

import se.ivankrizsan.monolithmicroservices.warehouse.exceptions.ProductNotInWarehouseException;

import java.util.Optional;

/**
 * The warehouse service represents a warehouse containing the products that are available for purchase.
 *
 * @author Ivan Krizsan
 */
public interface WarehouseService {
    /**
     * Reports the available inventory amount for the product in the warehouse with the supplied
     * product number.
     *
     * @param inProductNumber Product number of the product for which to report available inventory balance.
     * @return Available product inventory or empty if no matching product available.
     */
    Optional<Double> retrieveProductAvailableAmount(String inProductNumber);

    /**
     * Retrieves the unit price for the product in the warehouse with the supplied product number.
     *
     * @param inProductNumber Product number of the product which unit price to retrieve.
     * @return Product unit price or empty if product not found in warehouse.
     */
    Optional<Double> retrieveProductUnitPrice(String inProductNumber);

    /**
     * Reserves the supplied amount of the product in the warehouse with the supplied product number.
     * The product stock will be reduced with the amount to reserved if there is enough of
     * the product in stock.
     *
     * @param inProductNumber Product number of the product to reserve.
     * @param inAmount Amount of the product to reserve.
     * @return Reservation id if amount of product successfully reserved otherwise empty.
     */
    Optional<Long> reserveProduct(String inProductNumber, double inAmount);

    /**
     * Removes the product reservation with the supplied id.
     * Does nothing if there is no product reservation with the supplied id.
     * The semantics of this operation is that stock of the warehouse, which previously has been reserved,
     * is consumed without affecting the available stock level.
     *
     * @param inProductReservationId Id of product reservation which to remove.
     * @return True if product reservation was successfully removed, false otherwise.
     */
    boolean removeProductReservation(Long inProductReservationId);

    /**
     * Retrieves the reserved amount for the product reservation with the supplied id.
     *
     * @param inProductReservationId Product reservation id for which to retrieve reserved amount.
     * @return Reserved amount or empty if no product reservation exists.
     */
    Optional<Double> retrieveReservationAmount(Long inProductReservationId);

    /**
     * Creates the product with the supplied product number, the supplied name and the supplied unit
     * price in the warehouse setting its available and reserved amounts to zero.
     * Does nothing if a product with the supplied product number already exists in the warehouse.
     *
     * @param inProductNumber Product number of product to create. Must be unique in the warehouse.
     * @param inProductName Name of the product to create.
     * @param inUnitPrice Product's unit price.
     */
    void createProductInWarehouse(String inProductNumber, String inProductName, Double inUnitPrice);

    /**
     * Increases the warehouse stock of the product with the supplied product number with the supplied amount.
     *
     * @param inProductNumber Product number of product which stock to increase.
     * @param inAmount Amount to increase the product's stock.
     * @throws ProductNotInWarehouseException If the product does not exist in the warehouse.
     */
    void increaseProductStock(String inProductNumber, double inAmount) throws ProductNotInWarehouseException;
}
