package strategy;

import dao.AccountDAO;
import model.Account;
import model.TransactionRequest;
import java.sql.Connection;
import java.sql.SQLException;

public class TransferStrategy implements TransactionStrategy {
    @Override
    public void execute(TransactionRequest request, Connection conn) throws SQLException {
        AccountDAO dao = new AccountDAO(conn);
        Account from = dao.findById(request.getAccountId());
        Account to = dao.findById(request.getTargetAccountId());

        if (from == null || to == null) throw new IllegalStateException("Счет не найден");
        if (from.isFrozen() || to.isFrozen()) throw new IllegalStateException("Один из счетов заморожен");
        if (from.getBalance() < request.getAmount()) throw new IllegalStateException("Недостаточно средств");

        dao.updateBalance(from.getId(), from.getBalance() - request.getAmount());
        dao.updateBalance(to.getId(), to.getBalance() + request.getAmount());
    }
}