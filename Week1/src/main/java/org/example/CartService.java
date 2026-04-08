package org.example;

import java.sql.*;
import java.util.List;

public class CartService {
    public static void saveCart(int totalItems, double totalCost, String language,
                                List<ShoppingCart.Item> items) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO cart_records (total_items, total_cost, language) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, totalItems);
            ps.setDouble(2, totalCost);
            ps.setString(3, language);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int cartId = keys.getInt(1);
                PreparedStatement itemPs = conn.prepareStatement(
                        "INSERT INTO cart_items (cart_record_id, item_number, price, quantity, subtotal)" +
                        " VALUES (?, ?, ?, ?, ?)");
                for (int i = 0; i < items.size(); i++) {
                    ShoppingCart.Item item = items.get(i);
                    itemPs.setInt(1, cartId);
                    itemPs.setInt(2, i + 1);
                    itemPs.setDouble(3, item.price);
                    itemPs.setInt(4, item.quantity);
                    itemPs.setDouble(5, item.calculateCost());
                    itemPs.addBatch();
                }
                itemPs.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Failed to save cart: " + e.getMessage());
        }
    }
}
