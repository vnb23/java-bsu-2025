package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionRequest {
    private UUID id;
    private UUID accountId;
    private UUID targetAccountId;
    private ActionType type;
    private double amount;
    private LocalDateTime timestamp;

    public TransactionRequest(UUID accountId, ActionType type, double amount, UUID targetAccountId) {
        this.id = UUID.randomUUID();
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.targetAccountId = targetAccountId;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public UUID getTargetAccountId() { return targetAccountId; }
    public ActionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}