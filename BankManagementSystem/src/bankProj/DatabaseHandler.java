package bankProj;

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/bankdata";
    private static final String USER = "root";
    private static final String PASSWORD = "Uday@1234";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertCustomer(Customer customer) {
        String sql = "INSERT INTO customers (username, password, name, address, phone, balance) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.username);
            ps.setString(2, customer.password);
            ps.setString(3, customer.name);
            ps.setString(4, customer.address);
            ps.setString(5, customer.phone);
            ps.setDouble(6, customer.balance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomer(String username, String password) {
        String sql = "SELECT * FROM customers WHERE username = ?" + (password.equals("dummy") ? "" : " AND password = ?");
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            if (!password.equals("dummy")) ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Customer(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDouble("balance"),
                    new java.util.Date()
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        String sql = "SELECT username FROM customers WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateBalance(String username, double balance) {
        String sql = "UPDATE customers SET balance = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, balance);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateField(String username, String field, String newValue) {
        if (!isAllowedField(field)) {
            System.out.println("Invalid field update attempt: " + field);
            return;
        }
        String sql = "UPDATE customers SET " + field + " = ? WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newValue);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Optional safety to whitelist updatable fields
    private static boolean isAllowedField(String field) {
        return field.equals("name") || field.equals("address") || field.equals("phone") || field.equals("password");
    }
}
