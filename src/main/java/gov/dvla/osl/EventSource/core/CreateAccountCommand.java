package gov.dvla.osl.EventSource.core;

public class CreateAccountCommand {

    String accountId;

    int overdraftLimit;

    public CreateAccountCommand() {
    }

    public CreateAccountCommand(String accountId, int overdraftLimit) {
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
