package gov.dvla.osl.EventSource.core;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class WithdrawMoneyCommand {

    @TargetAggregateIdentifier
    String accountId;

    int amount;

    public WithdrawMoneyCommand() {
    }

    public WithdrawMoneyCommand(String accountId, int amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    public String getAccountId() {
        return accountId;
    }

    public int getAmount() {
        return amount;
    }
}
