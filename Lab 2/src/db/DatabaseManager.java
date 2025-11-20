package src.db;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String URL = "jdbc:h2:mem:bank_db;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void initTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts (" +
                    "id UUID PRIMARY KEY, " +
                    "owner_name VARCHAR(255), " +
                    "balance DOUBLE, " +
                    "is_frozen BOOLEAN)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM accounts");
            if (rs.next() && rs.getInt(1) == 0) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?, ?)");
                createAccount(ps, "GenshinFan2004", 1000.0);
                createAccount(ps, "Zhongli_Wallet", 0.0);
                createAccount(ps, "Northland_Bank", 999999.9);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createAccount(PreparedStatement ps, String owner, double balance) throws SQLException {
        ps.setObject(1, UUID.randomUUID());
        ps.setString(2, owner);
        ps.setDouble(3, balance);
        ps.setBoolean(4, false);
        ps.executeUpdate();
    }
}