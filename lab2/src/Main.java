import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Bank Application with SQLite...");

        try {
            db.DatabaseManager dbManager = db.DatabaseManager.getInstance();
            dbManager.initTables();
            dbManager.printAllAccounts();

        } catch (Exception e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.out.println("Continuing in demo mode without database...");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                BankFrame frame = new BankFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                System.out.println("UI started successfully");
            } catch (Exception e) {
                System.err.println("UI error: " + e.getMessage());

                JFrame simpleFrame = new JFrame("Bank System");
                simpleFrame.add(new JLabel("Basic UI - Check console for errors"));
                simpleFrame.setSize(300, 200);
                simpleFrame.setVisible(true);
            }
        });
    }
}