package visitor;

import model.ActionType;
import model.TransactionRequest;

public class AuditVisitor implements Visitor {
    private double totalVolume = 0;

    @Override
    public void visit(TransactionRequest request) {
        if (request.getType() == ActionType.DEPOSIT ||
                request.getType() == ActionType.WITHDRAW ||
                request.getType() == ActionType.TRANSFER) {
            totalVolume += request.getAmount();
        }
    }

    public double getTotalVolume() { return totalVolume; }
}