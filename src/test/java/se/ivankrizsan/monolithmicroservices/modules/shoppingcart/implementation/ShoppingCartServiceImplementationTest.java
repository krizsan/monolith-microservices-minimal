package se.ivankrizsan.monolithmicroservices.modules.shoppingcart.implementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import se.ivankrizsan.monolithmicroservices.modules.shoppingcart.api.ShoppingCartService;
import se.ivankrizsan.monolithmicroservices.modules.shoppingcart.configuration.ShoppingCartConfiguration;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.api.WarehouseService;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.configuration.WarehouseConfiguration;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.domain.ProductReservation;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.persistence.ProductRepository;
import se.ivankrizsan.monolithmicroservices.modules.warehouse.persistence.ProductReservationRepository;

import java.util.List;
import java.util.Optional;

/**
 * Tests the {@link ShoppingCartServiceImplementation}.
 *
 * @author Ivan Krizsan
 */
@DataJpaTest()
@ContextConfiguration(classes = { ShoppingCartConfiguration.class, WarehouseConfiguration.class })
class ShoppingCartServiceImplementationTest {
    /* Constant(s): */
    public final static String PRODUCTA_PRODUCTNUMBER = "12345-1";
    public final static double PRODUCTA_AVAILABLEAMOUNT = 100;
    public final static double PRODUCTA_UNITPRICE = 15.41;
    public final static String PRODUCTB_PRODUCTNUMBER = "54321-1";
    public final static double PRODUCTB_AVAILABLEAMOUNT = 50;
    public final static double PRODUCTB_UNITPRICE = 33.90;

    /* Instance variable(s): */
    @Autowired
    protected WarehouseService mWarehouseService;
    @Autowired
    protected ShoppingCartService mShoppingCartService;
    @Autowired
    protected ProductRepository mProductRepository;
    @Autowired
    protected ProductReservationRepository mProductReservationsRepository;

    /**
     * Sets up products in warehouse before each test.
     */
    @BeforeEach
    void setUpBeforeEachTest() {
        mShoppingCartService.emptyCart();

        mWarehouseService.createProductInWarehouse(
            PRODUCTA_PRODUCTNUMBER,
            "Product A",
            PRODUCTA_UNITPRICE);
        mWarehouseService.increaseProductStock(PRODUCTA_PRODUCTNUMBER, PRODUCTA_AVAILABLEAMOUNT);

        mWarehouseService.createProductInWarehouse(
            PRODUCTB_PRODUCTNUMBER,
            "Product B",
            PRODUCTB_UNITPRICE);
        mWarehouseService.increaseProductStock(PRODUCTB_PRODUCTNUMBER, PRODUCTB_AVAILABLEAMOUNT);
    }

    /**
     * Tests adding all the remaining stock of a product to the shoppingcart.
     * Expected result:
     * All the remaining stock of the product should have been placed in the cart.
     * All the stock of the product should be reserved.
     * There should be no remaining available stock of the product.
     */
    @Test
    void addItemEntireStockToCartTest() {
        final boolean theAddItemSuccessFlag = mShoppingCartService.addItemToCart(
            PRODUCTA_PRODUCTNUMBER, PRODUCTA_AVAILABLEAMOUNT);
        Assertions.assertTrue(theAddItemSuccessFlag,
            "It should be possible to add all the remaining stock for a product to the shopping cart");

        final Optional<Double> theRemainingAvailableAmountOptional =
            mWarehouseService.retrieveProductAvailableAmount(PRODUCTA_PRODUCTNUMBER);
        Assertions.assertTrue(theRemainingAvailableAmountOptional.isPresent());
        Assertions.assertEquals(0, theRemainingAvailableAmountOptional.get(),
            "There should be no remaining available stock of the product");

        final List<ProductReservation> theProductAReservations =
            mProductReservationsRepository.findAllByProductNumber(PRODUCTA_PRODUCTNUMBER);
        Assertions.assertEquals(1, theProductAReservations.size(),
            "There should be a single product reservation");
        final ProductReservation theProductAReservation = theProductAReservations.get(0);
        Assertions.assertEquals(PRODUCTA_AVAILABLEAMOUNT, theProductAReservation.getReservedAmount(),
            "All of the stock of the product should be reserved");
    }

    /**
     * Tests adding more than the remaining stock of a product to the shoppingcart.
     * Expected result:
     * Adding the product to the shoppingcart should fail.
     * There should be no reservations of the product.
     * The available amount of the product in the warehouse should remain unchanged.
     */
    @Test
    void addItemAmountNotInStockToCartTest() {
        final boolean theAddItemSuccessFlag = mShoppingCartService.addItemToCart(
            PRODUCTA_PRODUCTNUMBER, PRODUCTA_AVAILABLEAMOUNT * 2);
        Assertions.assertFalse(theAddItemSuccessFlag,
            "It should not be possible to add more than the available stock of a product to the shoppingcart");

        final Optional<Double> theRemainingAvailableAmountOptional =
            mWarehouseService.retrieveProductAvailableAmount(PRODUCTA_PRODUCTNUMBER);
        Assertions.assertTrue(theRemainingAvailableAmountOptional.isPresent());
        Assertions.assertEquals(PRODUCTA_AVAILABLEAMOUNT, theRemainingAvailableAmountOptional.get(),
            "The available amount in the warehouse should remain unchanged");

        final List<ProductReservation> theProductAReservations =
            mProductReservationsRepository.findAllByProductNumber(PRODUCTA_PRODUCTNUMBER);
        Assertions.assertEquals(0, theProductAReservations.size(),
            "There should be no reservations for the product");
    }

    /**
     * Tests calculating the price of the products in the shopping cart.
     * Expected result:
     * The total price of the products in the shopping cart should be correctly calculated.
     */
    @Test
    void calculateCartPriceTest() {
        addTwoProductsToShoppingCart();

        final Double theCartPrice = mShoppingCartService.calculateCartPrice();
        final double theExpectedCartPrice = PRODUCTA_AVAILABLEAMOUNT * PRODUCTA_UNITPRICE + PRODUCTB_UNITPRICE;
        Assertions.assertEquals(theExpectedCartPrice,
                theCartPrice,
                "The total price of the products in the shopping cart should be correctly calculated");
    }

    private void addTwoProductsToShoppingCart() {
        final boolean theAddItemASuccessFlag = mShoppingCartService.addItemToCart(
                PRODUCTA_PRODUCTNUMBER, PRODUCTA_AVAILABLEAMOUNT);
        Assertions.assertTrue(theAddItemASuccessFlag,
            "It should be possible to add Product A to the shoppingcart");

        final boolean theAddItemBSuccessFlag = mShoppingCartService.addItemToCart(
            PRODUCTB_PRODUCTNUMBER, 1);
        Assertions.assertTrue(theAddItemBSuccessFlag,
            "It should be possible to add Product B to the shoppingcart");
    }
}