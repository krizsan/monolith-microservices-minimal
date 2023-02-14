package se.ivankrizsan.monolithmicroservices.shoppingcart.api;

/**
 * The shopping cart service manages a shopping cart.
 * Note: In its current incarnation, the service only manages a single shopping cart at a time.
 *
 * @author Ivan Krizsan
 */
public interface ShoppingCartService {

    /**
     * Adds the supplied amount of the product with supplied product number to the shopping cart.
     *
     * @param inProductNumber Product number of product to add to the cart.
     * @param inAmount Amount of the product to add to the cart.
     * @return True if item successfully added to shopping cart, false otherwise.
     */
    boolean addItemToCart(String inProductNumber, double inAmount);

    /**
     * Empties the shopping cart.
     */
    void emptyCart();

    /**
     * Calculates the total price of the items in the shopping cart.
     * Shipping cost, any additional fees etc are not included in the calculated price.
     *
     * @return Total price of items in the cart.
     */
    Double calculateCartPrice();
}
