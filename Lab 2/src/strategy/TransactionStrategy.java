package src.strategy;

import com.genshinbank.model.TransactionRequest;
import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionStrategy {
    void execute(TransactionRequest request, Connection conn) throws SQLException, IllegalStateException;
}