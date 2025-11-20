package strategy;

import dao.AccountDAO;
import model.TransactionRequest;
import java.sql.Connection;
import java.sql.SQLException;

public class FreezeStrategy implements TransactionStrategy {
    @Override
    public void execute(TransactionRequest request, Connection conn) throws SQLException {
        AccountDAO dao = new AccountDAO(conn);
        dao.updateStatus(request.getAccountId(), true);
    }
}