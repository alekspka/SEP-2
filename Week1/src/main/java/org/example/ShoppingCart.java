package org.example;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.ArrayList;

public class ShoppingCart {
    private ArrayList<Item> items;

    public ShoppingCart() {
        items = new ArrayList<>();
    }

    public class Item {
        String product;
        double price;
        int quantity;

        Item(String product, double price, int quantity) {
            this.product = product;
            this.price = price;
            this.quantity = quantity;
        }

        public double calculateCost() {
            return price * quantity;
        }
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public double calculateTotal() {
        double total = 0;
        for (Item item : items) {
            total += item.calculateCost();
        }
        return total;
    }

    public static void main(String[] args) {
        try {

            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Could not set UTF-8 encoding");
        }

        System.out.println("Select a language:");
        System.out.println("1. English");
        System.out.println("2. Finnish");
        System.out.println("3. Japanese");

        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        int choice = Integer.parseInt(scanner.nextLine().trim());
        Locale locale;

        switch (choice) {
            case 1:
                locale = new Locale("en", "US");
                break;
            case 2:
                locale = new Locale("fi", "FI");
                break;
            case 3:
                locale = new Locale("ja", "JP");
                break;
            default:
                System.out.println("Invalid choice. Defaulting to English.");
                locale = new Locale("en", "US");
                break;
        }

        ResourceBundle rb;
        try {
            rb = ResourceBundle.getBundle("messages", locale);
        } catch (Exception e) {
            System.out.println("Error loading language pack. Defaulting to English.");
            rb = ResourceBundle.getBundle("messages", new Locale("en", "US"));
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        ShoppingCart cart = new ShoppingCart();

        while (true) {
            System.out.println(rb.getString("product"));
            String product = scanner.nextLine();

            if (product.equals("q")) {
                break;
            }

            System.out.println(rb.getString("price"));
            double price = scanner.nextDouble();

            System.out.println(rb.getString("quantity"));
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Item item = cart.new Item(product, price, quantity);
            cart.addItem(item);
            System.out.println(rb.getString("itemAdded") + "\n");
        }

        String formattedTotal = MessageFormat.format(rb.getString("total"),
                                                      currencyFormatter.format(cart.calculateTotal()));
        System.out.println("\n" + formattedTotal);
        scanner.close();
    }
}