package model;

import java.util.UUID;

public class Account {
    private UUID id;
    private String ownerName;
    private double balance;
    private boolean isFrozen;

    public Account(UUID id, String ownerName, double balance, boolean isFrozen) {
        this.id = id;
        this.ownerName = ownerName;
        this.balance = balance;
        this.isFrozen = isFrozen;
    }

    public UUID getId() { return id; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public boolean isFrozen() { return isFrozen; }

    @Override
    public String toString() {
        return String.format("%s [%s] - %.2f $", ownerName, isFrozen ? "FROZEN" : "ACTIVE", balance);
    }
}