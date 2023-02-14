package se.ivankrizsan.monolithmicroservices.shoppingcart.implementation;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import se.ivankrizsan.monolithmicroservices.shoppingcart.api.ShoppingCartService;
import se.ivankrizsan.monolithmicroservices.warehouse.api.WarehouseService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the {@link ShoppingCartService}.
 *
 * @author Ivan Krizsan
 */
@RequiredArgsConstructor
@NoArgsConstructor
public class ShoppingCartServiceImplementation implements ShoppingCartService {
    /** Warehouse from which items placed in the shopping cart will be taken. */
    @NonNull
    protected WarehouseService mWarehouseService;
    /** Product reservations associated with the shopping cart. */
    protected MultiValueMap<String, Long> mProductReservationIds = new LinkedMultiValueMap<>() {};

    @Transactional
    @Override
    public boolean addItemToCart(final String inProductNumber, final double inAmount) {
        final Optional<Long> theReservationIdOptional = mWarehouseService.reserveProduct(inProductNumber, inAmount);
        if (theReservationIdOptional.isPresent()) {
            mProductReservationIds.add(inProductNumber, theReservationIdOptional.get());
            return true;
        }

        return false;
    }

    @Override
    public void emptyCart() {
        mProductReservationIds.clear();
    }

    @Override
    public Double calculateCartPrice() {
        double theCartPrice = 0.0;

        for (Map.Entry<String, List<Long>> theProductIdReservationsEntry : mProductReservationIds.entrySet()) {
            final String theProductNumber = theProductIdReservationsEntry.getKey();
            final Optional<Double> theProductPriceOptional = mWarehouseService.retrieveProductUnitPrice(theProductNumber);

            if (theProductPriceOptional.isPresent()) {
                final Double theProductPrice = theProductPriceOptional.get();

                final List<Long> theProductReservations = theProductIdReservationsEntry.getValue();
                for (Long theProductReservation : theProductReservations) {
                    final Optional<Double> theReservationAmountOptional =
                        mWarehouseService.retrieveReservationAmount(theProductReservation);
                    if (theReservationAmountOptional.isPresent()) {
                        theCartPrice = theCartPrice + (theProductPrice * theReservationAmountOptional.get());
                    }
                }
            } else {
                throw new RuntimeException("No price found for product with number " + theProductNumber);
            }
        }
        return theCartPrice;
    }
}
