package factory;

import model.ActionType;
import strategy.*;

public class TransactionFactory {
    public static TransactionStrategy getStrategy(ActionType type) {
        switch (type) {
            case DEPOSIT: return new DepositStrategy();
            case WITHDRAW: return new WithdrawStrategy();
            case FREEZE: return new FreezeStrategy();
            case TRANSFER: return new TransferStrategy();
            default: throw new IllegalArgumentException("Неизвестная операция");
        }
    }
}