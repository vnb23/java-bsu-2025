package strategy;

import dao.AccountDAO;
import model.Account;
import model.TransactionRequest;
import java.sql.Connection;
import java.sql.SQLException;

public class DepositStrategy implements TransactionStrategy {
    @Override
    public void execute(TransactionRequest request, Connection conn) throws SQLException {
        AccountDAO dao = new AccountDAO(conn);
        Account acc = dao.findById(request.getAccountId());
        if (acc.isFrozen()) throw new IllegalStateException("Счет заморожен!");

        dao.updateBalance(request.getAccountId(), acc.getBalance() + request.getAmount());
    }
}