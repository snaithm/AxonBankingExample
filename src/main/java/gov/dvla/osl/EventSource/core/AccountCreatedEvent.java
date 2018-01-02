package gov.dvla.osl.EventSource.core;

public class AccountCreatedEvent {

    String accountId;

    int overdraftLimit;

    public AccountCreatedEvent() {
    }

    public AccountCreatedEvent(String accountId, int overdraftLimit) {
        this.accountId = accountId;
        this.overdraftLimit = overdraftLimit;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getOverdraftLimit() {
        return overdraftLimit;
    }
}
