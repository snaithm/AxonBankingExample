package gov.dvla.osl.EventSource.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.BasicDBObject;;
import com.mongodb.client.MongoCollection;
import gov.dvla.osl.EventSource.core.*;
import gov.dvla.osl.EventSource.exceptions.AccountExistsException;
import gov.dvla.osl.EventSource.exceptions.OverdraftLimitExceededException;
import io.swagger.annotations.ApiParam;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.bson.Document;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;

import static com.mongodb.client.model.Filters.eq;
import static gov.dvla.osl.EventSource.db.DbValues.collectionName;
import static gov.dvla.osl.EventSource.db.DbValues.readModelDBName;
import static gov.dvla.osl.EventSource.db.DbValues.mongoClient;
import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

public class Account {

    @AggregateIdentifier
    @NotNull
    private String accountId;
    private int balance;
    private int overdraftLimit = 0;

    public Account() {

    }

    @CommandHandler
    public Account(CreateAccountCommand command) throws AccountExistsException {

        if (!accountExists(command.getAccountId())) {
            apply(new AccountCreatedEvent(command.getAccountId(), command.getOverdraftLimit()));

        } else {
            throw new AccountExistsException();
        }
    }

    @CommandHandler
    public void handleWithdrawCommand (WithdrawMoneyCommand command) throws OverdraftLimitExceededException {

        if (balance + overdraftLimit >= command.getAmount()) {
            apply(new MoneyWithdrawnEvent(accountId, command.getAmount(), balance - command.getAmount()));

        } else {
            throw new OverdraftLimitExceededException();
        }
    }

    @CommandHandler
    public void handleDepositCommand (DepositMoneyCommand command)  {

        apply(new MoneyDepositedEvent(accountId, command.getAmount(), balance + command.getAmount()));
    }

    @EventHandler
    public void onEventForDo (AccountCreatedEvent event) {

        this.accountId = event.getAccountId();
        this.overdraftLimit = event.getOverdraftLimit();
        addAccount(this.accountId, this.overdraftLimit);
    }

    @EventHandler
    public void onEventForDo (MoneyWithdrawnEvent event) {

        this.balance = event.getBalance();
        updateAccount(this.accountId, this.balance);
    }

    @EventHandler
    public void onEventForDo (MoneyDepositedEvent event) {

        this.balance = event.getBalance();
        updateAccount(this.accountId, this.balance);
    }

    private void addAccount(String accountId, int overdraftLimit) {

        if (accountExists(accountId)) return;
        MongoCollection<Document> collection = mongoClient.getDatabase(readModelDBName).getCollection(collectionName);
        Document doc = new Document();
        doc.put("accountId", accountId);
        doc.put("balance", 0);
        doc.put("overdraftLimit", overdraftLimit);
        collection.insertOne(doc);
    }

    private void updateAccount(String accountId, int balance) {

        MongoCollection<Document> collection = mongoClient.getDatabase(readModelDBName).getCollection(collectionName);
        BasicDBObject newDoc = new BasicDBObject();
        newDoc.append("$set", new BasicDBObject().append("balance", balance));
        BasicDBObject existingDoc = new BasicDBObject().append("accountId", accountId);
        collection.updateOne(existingDoc, newDoc);
    }

    private Boolean accountExists(String accountId) {

        MongoCollection<Document> collection = mongoClient.getDatabase(readModelDBName).getCollection(collectionName);
        Document existingAccount = collection.find(eq("accountId", accountId)).first();
        return existingAccount != null;
    }

    @JsonProperty
    public String getAccountId() {
        return accountId;
    }

    @JsonProperty
    public int getBalance() {
        return balance;
    }

    @JsonProperty
    public int getOverdraftLimit() {
        return overdraftLimit;
    }
}