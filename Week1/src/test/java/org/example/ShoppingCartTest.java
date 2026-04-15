package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartTest {
    private static final String APPLE = "Apple";
    private static final InputStream originalIn = System.in;
    private static final PrintStream originalOut = new PrintStream(new FileOutputStream(FileDescriptor.out));
    private static final Locale originalLocale = Locale.getDefault();

    @AfterEach
    void restoreSystemStreams() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Locale.setDefault(originalLocale);
    }

    @Test
    void itemCalculateCostReturnsPriceTimesQuantity() {
        ShoppingCart cart = new ShoppingCart();
        ShoppingCart.Item item = cart.new Item(APPLE, 2.5, 4);

        assertEquals(10.0, item.calculateCost(), 0.0001);
    }

    @Test
    void calculateTotalReturnsZeroForEmptyCart() {
        ShoppingCart cart = new ShoppingCart();

        assertEquals(0.0, cart.calculateTotal(), 0.0001);
    }

    @Test
    void calculateTotalReturnsPriceTimesQuantityForSingleItem() {
        ShoppingCart cart = new ShoppingCart();
        ShoppingCart.Item item = cart.new Item(APPLE, 2.5, 2);

        cart.addItem(item);

        assertEquals(5.0, cart.calculateTotal(), 0.0001);
    }

    @Test
    void calculateTotalReturnsSumForMultipleItems() {
        ShoppingCart cart = new ShoppingCart();

        cart.addItem(cart.new Item("Milk", 1.2, 3));
        cart.addItem(cart.new Item("Bread", 2.0, 1));

        assertEquals(5.6, cart.calculateTotal(), 0.0001);
    }

    @Test
    void addItemAndClearUpdateCartContents() {
        ShoppingCart cart = new ShoppingCart();
        ShoppingCart.Item apples = cart.new Item(APPLE, 2.5, 2);

        cart.addItem(apples);

        assertEquals(1, cart.getItems().size());
        assertEquals(apples, cart.getItems().getFirst());

        cart.clear();

        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void mainUsesSelectedLanguageAndPrintsFormattedTotal() {
        Locale.setDefault(Locale.US);
        char decimalSeparator = DecimalFormatSymbols.getInstance(Locale.US).getDecimalSeparator();
        String input = String.join(System.lineSeparator(),
                "1",
                APPLE,
                "2" + decimalSeparator + "5",
                "2",
                "q") + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        ShoppingCart.main(new String[0]);

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Product name:"));
        assertTrue(console.contains("Item added!"));
        assertTrue(console.contains("Total cost:"));
        assertTrue(console.contains("5.00"));
    }

    @Test
    void mainFallsBackToEnglishForInvalidLanguageChoice() {
        Locale.setDefault(Locale.US);
        String input = String.join(System.lineSeparator(),
                "9",
                "q") + System.lineSeparator();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        ShoppingCart.main(new String[0]);

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Invalid choice. Defaulting to English."));
        assertTrue(console.contains("Product name:"));
        assertFalse(console.isBlank());
    }
}
