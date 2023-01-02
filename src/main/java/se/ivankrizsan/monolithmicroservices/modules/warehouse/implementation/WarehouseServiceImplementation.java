package se.ivankrizsan.monolithmicroservices.modules.warehouse.implementation;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.api.WarehouseService;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.domain.Product;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.domain.ProductReservation;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.exceptions.ProductNotInWarehouseException;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.persistence.ProductRepository;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.persistence.ProductReservationRepository;

import java.util.Optional;

/**
 * Implementation of the {@link WarehouseService}.
 *
 * @author Ivan Krizsan
 */
@RequiredArgsConstructor
public class WarehouseServiceImplementation implements WarehouseService {
    /* Constant(s): */

    /* Instance variable(s): */
    @NonNull
    protected ProductRepository mProductRepository;
    @NonNull
    protected ProductReservationRepository mProductReservationRepository;

    @Override
    public Optional<Double> retrieveProductAvailableAmount(final String inProductNumber) {
        Assert.hasText(inProductNumber, "A product number is required");
        final Optional<Product> theProductOptional = mProductRepository.findByProductNumber(inProductNumber);
        if (theProductOptional.isEmpty()) {
            throw new ProductNotInWarehouseException(inProductNumber);
        }
        return theProductOptional.map(Product::availableAmount);
    }

    @Override
    public Optional<Double> retrieveProductUnitPrice(final String inProductNumber) {
        Assert.hasText(inProductNumber, "A product number is required");
        final Optional<Product> theProductOptional = mProductRepository.findByProductNumber(inProductNumber);
        if (theProductOptional.isEmpty()) {
            throw new ProductNotInWarehouseException(inProductNumber);
        }
        return theProductOptional.map(Product::unitPrice);
    }

    @Override
    @Transactional
    public Optional<Long> reserveProduct(final String inProductNumber, final double inAmount) {
        Assert.hasText(inProductNumber, "A product number is required");

        final Optional<Product> theProductOptional = mProductRepository.findByProductNumber(inProductNumber);
        if (theProductOptional.isEmpty()) {
            throw new ProductNotInWarehouseException(inProductNumber);
        } else if (theProductOptional.get().availableAmount() < inAmount) {
            /* Insufficient product amount available - cannot reserve. */
            return Optional.empty();
        } else {
            /* Reduce the available amount and increase the reserved amount. */
            Product theProductToReserve = theProductOptional.get();
            final double theNewAvailableAmount = theProductToReserve.availableAmount() - inAmount;
            final double theNewReservedAmount = theProductToReserve.reservedAmount() + inAmount;
            theProductToReserve.availableAmount(theNewAvailableAmount);
            theProductToReserve.reservedAmount(theNewReservedAmount);

            /* Create a product reservation for the amount. */
            final ProductReservation theProductReservation = new ProductReservation(inProductNumber, inAmount);
            mProductReservationRepository.save(theProductReservation);

            return Optional.of(theProductReservation.getId());
        }
    }

    @Override
    public Optional<Double> retrieveReservationAmount(final Long inProductReservationId) {
        final Optional<ProductReservation> theProductReservationOptional =
            mProductReservationRepository.findById(inProductReservationId);
        return theProductReservationOptional.map(ProductReservation::getReservedAmount);
    }

    @Override
    public boolean removeProductReservation(final Long inProductReservationId) {
        final Optional<ProductReservation> theProductReservationOptional =
            mProductReservationRepository.findById(inProductReservationId);

        if (theProductReservationOptional.isPresent()) {
            mProductReservationRepository.deleteById(inProductReservationId);
        }

        return theProductReservationOptional.isPresent();
    }

    @Override
    public void createProductInWarehouse(final String inProductNumber,
                                         final String inProductName,
                                         final Double inProductUnitPrice) {
        Assert.hasText(inProductNumber, "A product number is required");
        Assert.hasText(inProductName, "A product name is required");
        Assert.notNull(inProductUnitPrice, "A product unit price is required");
        if (!mProductRepository.existsByProductNumber(inProductNumber)) {
            final Product theNewProduct = new Product()
                .productNumber(inProductNumber)
                .name(inProductName)
                .availableAmount(0)
                .reservedAmount(0)
                .unitPrice(inProductUnitPrice);
            mProductRepository.save(theNewProduct);
        }
    }

    @Override
    public void increaseProductStock(final String inProductNumber, final double inAmount)
        throws ProductNotInWarehouseException {
        final Optional<Product> theProductOptional = mProductRepository.findByProductNumber(inProductNumber);
        if (theProductOptional.isEmpty()) {
            throw new ProductNotInWarehouseException(inProductNumber);
        }

        /* Increase the product's available amount. */
        final Product theProduct = theProductOptional.get();
        final double theNewProductAmount = theProduct.availableAmount() + inAmount;
        theProduct.availableAmount(theNewProductAmount);
        mProductRepository.save(theProduct);
    }
}
