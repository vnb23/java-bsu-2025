import dao.AccountDAO;
import db.DatabaseManager;
import model.Account;
import model.ActionType;
import model.TransactionRequest;
import service.AsyncTransactionProcessor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BankFrame extends JFrame {
    private final AsyncTransactionProcessor processor = new AsyncTransactionProcessor();
    private final JTextArea logArea = new JTextArea();
    private final DefaultTableModel tableModel;
    private final AccountDAO accountDAO;

    public BankFrame() {
        setTitle("Genshin Bank System (Enterprise Edition)");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            accountDAO = new AccountDAO(DatabaseManager.getInstance().getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String[] columns = {"UUID", "Владелец", "Баланс", "Статус"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable accountTable = new JTable(tableModel);
        add(new JScrollPane(accountTable), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        createButton(controls, "Новый счет", this::handleCreateAccount);

        controls.add(new JSeparator());

        createButton(controls, "Пополнить", () -> handleSimpleAction(ActionType.DEPOSIT));
        createButton(controls, "Снять", () -> handleSimpleAction(ActionType.WITHDRAW));
        createButton(controls, "Заморозить", () -> handleSimpleAction(ActionType.FREEZE));
        createButton(controls, "Перевод", this::handleTransfer);
        createButton(controls, "Отчет (Audit)", () ->
                DialogHelper.showInfo(this, "Всего обработано средств: " + processor.getAuditTotal() + " $")
        );

        add(controls, BorderLayout.EAST);

        logArea.setRows(7);
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        processor.addObserver(msg -> SwingUtilities.invokeLater(() -> {
            logArea.append(LocalDateTime.now() + ": " + msg + "\n");
            refreshTable();
        }));

        refreshTable();
    }

    private void createButton(JPanel panel, String text, Runnable action) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.addActionListener(e -> action.run());
        panel.add(btn);
        panel.add(Box.createVerticalStrut(10));
    }

    private void handleCreateAccount() {
        String ownerName = JOptionPane.showInputDialog(this, "Введите имя владельца:", "Создание счета", JOptionPane.QUESTION_MESSAGE);

        if (ownerName == null || ownerName.trim().isEmpty()) {
            return;
        }

        double initialBalance = DialogHelper.askAmount(this);

        if (initialBalance < 0) return;

        try {
            accountDAO.createAccount(ownerName, initialBalance);

            logArea.append("Создан новый счет для: " + ownerName + "\n");
            refreshTable();

        } catch (SQLException e) {
            DialogHelper.showError(this, "Ошибка базы данных: " + e.getMessage());
        }
    }

    private void handleSimpleAction(ActionType type) {
        String idStr = DialogHelper.askUUID(this, "Введите UUID счета:");
        if (idStr == null) return;

        double amount = 0;
        if (type == ActionType.DEPOSIT || type == ActionType.WITHDRAW) {
            amount = DialogHelper.askAmount(this);
            if (amount < 0) return;
        }

        try {
            processor.submit(new TransactionRequest(
                    UUID.fromString(idStr), type, amount, null
            ));
        } catch (Exception ex) {
            DialogHelper.showError(this, "Ошибка создания транзакции: " + ex.getMessage());
        }
    }

    private void handleTransfer() {
        String fromId = DialogHelper.askUUID(this, "UUID Отправителя:");
        if (fromId == null) return;

        String toId = DialogHelper.askUUID(this, "UUID Получателя:");
        if (toId == null) return;

        double amount = DialogHelper.askAmount(this);
        if (amount < 0) return;

        try {
            processor.submit(new TransactionRequest(
                    UUID.fromString(fromId), ActionType.TRANSFER, amount, UUID.fromString(toId)
            ));
        } catch (Exception ex) {
            DialogHelper.showError(this, "Ошибка перевода: " + ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Account> accounts = accountDAO.findAll();
        for (Account a : accounts) {
            tableModel.addRow(new Object[]{
                    a.getId(), a.getOwnerName(), a.getBalance(), a.isFrozen() ? "ЗАМОРОЖЕН" : "АКТИВЕН"
            });
        }
    }
}