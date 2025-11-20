package db;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String URL = "jdbc:sqlite:bank.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite driver not found");
        }
    }

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + "?journal_mode=WAL&synchronous=NORMAL");
    }

    public void initTables() {
        System.out.println("Initializing database tables...");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "id TEXT PRIMARY KEY, " +
                    "owner_name VARCHAR(255) NOT NULL, " +
                    "balance REAL DEFAULT 0.0, " +
                    "is_frozen BOOLEAN DEFAULT FALSE)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM accounts");
            int rowCount = 0;
            if (rs.next()) {
                rowCount = rs.getInt("count");
            }

            if (rowCount == 0) {
                System.out.println("Adding test data to database...");
                conn.setAutoCommit(false);
                try {
                    createAccountInTransaction(conn, "GenshinFan2004", 1000.0);
                    createAccountInTransaction(conn, "Zhongli_Wallet", 0.0);
                    createAccountInTransaction(conn, "Northland_Bank", 999999.9);
                    conn.commit();
                    System.out.println("Test data added successfully");
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(true);
                }
            } else {
                System.out.println("Database already contains " + rowCount + " accounts");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void createAccountInTransaction(Connection conn, String owner, double balance) throws SQLException {
        String sql = "INSERT OR IGNORE INTO accounts (id, owner_name, balance) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, owner);
            ps.setDouble(3, balance);
            ps.executeUpdate();
        }
    }

    public void printAllAccounts() {
        String sql = "SELECT id, owner_name, balance, is_frozen FROM accounts ORDER BY owner_name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Accounts in database:");
            int count = 0;
            while (rs.next()) {
                String id = rs.getString("id");
                String owner = rs.getString("owner_name");
                double balance = rs.getDouble("balance");
                boolean frozen = rs.getBoolean("is_frozen");

                System.out.printf(" %d. %s (ID: %s...): %.2f %s%n",
                        ++count, owner, id.substring(0, 8), balance, frozen ? "❄️" : "✅");
            }

            if (count == 0) {
                System.out.println("No accounts found");
            }

        } catch (SQLException e) {
            System.err.println("Error reading accounts: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public ResultSet getAccountsForUI() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT id, owner_name, balance, is_frozen FROM accounts ORDER BY owner_name");
    }
}