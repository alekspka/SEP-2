package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShoppingCartTest {

    @Test
    public void calculateTotalReturnsZeroForEmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(0.0, cart.calculateTotal(), 0.0001);
    }

    @Test
    public void calculateTotalReturnsPriceTimesQuantityForSingleItem() {
        ShoppingCart cart = new ShoppingCart();
        ShoppingCart.Item item = cart.new Item("Apple", 2.5, 2);

        cart.addItem(item);

        assertEquals(5.0, cart.calculateTotal(), 0.0001);
    }

    @Test
    public void calculateTotalReturnsSumForMultipleItems() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem(cart.new Item("Milk", 1.2, 3));
        cart.addItem(cart.new Item("Bread", 2.0, 1));

        assertEquals(5.6, cart.calculateTotal(), 0.0001);
    }
}
