package visitor;

import model.TransactionRequest;

public interface Visitor {
    void visit(TransactionRequest request);
}