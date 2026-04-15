package org.example;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {
    public static Map<String, String> loadStrings(String language) {
        return loadStrings(language, DatabaseConnection::getConnection);
    }

    static Map<String, String> loadStrings(String language, SqlConnectionSupplier connectionSupplier) {
        Map<String, String> strings = new HashMap<>();
        try (Connection conn = connectionSupplier.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT `key`, value FROM localization_strings WHERE language = ?")) {
            ps.setString(1, language);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    strings.put(rs.getString("key"), rs.getString("value"));
                }
            }
        } catch (SQLException e) {
            System.err.println("DB localization failed, using fallback: " + e.getMessage());
        }
        return strings;
    }
}
