package gov.dvla.osl.EventSource.core;

public class MoneyDepositedEvent {

    String accountId;

    int amount;

    int balance;

    public MoneyDepositedEvent() {
    }

    public MoneyDepositedEvent(String accountId, int amount, int balance) {
        this.accountId = accountId;
        this.amount = amount;
        this.balance = balance;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getAmount() {
        return amount;
    }

    public int getBalance() {
        return balance;
    }
}
