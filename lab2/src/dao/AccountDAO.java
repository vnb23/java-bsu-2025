package dao;

import db.DatabaseManager;
import model.Account;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountDAO {
    private final Connection connection;

    public AccountDAO(Connection connection) {
        this.connection = connection;
    }

    public Account findById(UUID id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            ps.setString(1, id.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Account(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("owner_name"),
                        rs.getDouble("balance"),
                        rs.getBoolean("is_frozen")
                );
            }
        }
        return null;
    }

    public void updateBalance(UUID id, double newBalance) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?")) {
            ps.setDouble(1, newBalance);
            ps.setString(2, id.toString());
            ps.executeUpdate();
        }
    }

    public void updateStatus(UUID id, boolean isFrozen) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE accounts SET is_frozen = ? WHERE id = ?")) {
            ps.setBoolean(1, isFrozen);
            ps.setString(2, id.toString());
            ps.executeUpdate();
        }
    }

    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();

        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM accounts")) {

            while (rs.next()) {
                list.add(new Account(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("owner_name"),
                        rs.getDouble("balance"),
                        rs.getBoolean("is_frozen")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public void createAccount(String ownerName, double initialBalance) throws SQLException {
        String sql = "INSERT INTO accounts (id, owner_name, balance, is_frozen) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, ownerName);
            ps.setDouble(3, initialBalance);
            ps.setBoolean(4, false);

            ps.executeUpdate();
        }
    }
}