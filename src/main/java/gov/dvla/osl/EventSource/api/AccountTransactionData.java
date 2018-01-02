package gov.dvla.osl.EventSource.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class AccountTransactionData {

    @NotNull
    private String accountId;

    private int amount;

    public AccountTransactionData() {
    }

    public AccountTransactionData(String accountId, int amount) {
        this.accountId = accountId;
        this.amount = amount;
    }

    @JsonProperty
    public String getAccountId() {
        return accountId;
    }

    @JsonProperty
    public int getAmount() {
        return amount;
    }
}
