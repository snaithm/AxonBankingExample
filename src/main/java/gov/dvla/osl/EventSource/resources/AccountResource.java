package gov.dvla.osl.EventSource.resources;

import gov.dvla.osl.EventSource.api.Account;
import gov.dvla.osl.EventSource.api.AccountTransactionData;
import gov.dvla.osl.EventSource.core.CreateAccountCommand;
import gov.dvla.osl.EventSource.core.DepositMoneyCommand;
import gov.dvla.osl.EventSource.core.WithdrawMoneyCommand;
import gov.dvla.osl.EventSource.exceptions.AccountExistsException;
import io.swagger.annotations.*;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.documentpercommit.DocumentPerCommitStorageStrategy;
import org.axonframework.serialization.json.JacksonSerializer;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import static gov.dvla.osl.EventSource.db.DbValues.*;
import static org.axonframework.commandhandling.GenericCommandMessage.asCommandMessage;

@SwaggerDefinition(
    info = @Info(
        title = "Axon Banking Example",
        version = "0.0.1",
        description ="An example web service which can create bank accounts and perform basic deposit and withdrawal operations. " +
        "Axon framework is integrated to issue commands and capture events in a mongo event store whilst preserving the account details " +
        "in a separate database",
        contact = @Contact(name = "Matthew Snaith ", email = "mathswan1984@gmail.com")
    )
)

@Path("/banking")
@Api(value = "Banking Operations", description = "Example web service to manage bank accounts")
public class AccountResource {

    private Configuration config;

    public AccountResource() {

        final MongoEventStorageEngine eventStorageEngine = mongoEventStorageEngine();

        config = DefaultConfigurer.defaultConfiguration()
                    .configureAggregate(Account.class)
                    .configureCommandBus(c -> new SimpleCommandBus())
                    .configureEmbeddedEventStore(c -> eventStorageEngine)
                    .buildConfiguration();
        config.start();
    }

    private MongoEventStorageEngine mongoEventStorageEngine() {

        return new MongoEventStorageEngine(
                        new JacksonSerializer(),
                        null,
                        mongoTemplate(),
                        new DocumentPerCommitStorageStrategy());
    }

    private MongoTemplate mongoTemplate() {

        return new DefaultMongoTemplate(mongoClient, eventDBName, collectionName, snapshotEventsCollectionName);
    }

    @GET
    @Path("/alive")
    @ApiOperation(value = "Get the application status", notes="Indicates if the service is responisve or not")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Service is responsive")
    })
    public Response isAlive() {
        return Response.ok().build();
    }

    @POST
    @Path("/create")
    @ApiOperation(value = "Create a new bank account", notes = "Create a new bank account provided the accountId is not in use")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account created successfully"),
            @ApiResponse(code = 409, message = "Account conflict, accountId exists"),
            @ApiResponse(code = 400, message = "Invalid request received"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(@ApiParam(value="account to create", name="account") Account account) throws AccountExistsException {

        CreateAccountCommand createCommand = new CreateAccountCommand(account.getAccountId(), account.getOverdraftLimit());
        config.commandBus().dispatch(asCommandMessage(createCommand));
        return Response.ok().build();
    }

    @POST
    @Path("/withdraw")
    @ApiOperation(value = "Withdraw money from an existing bank account", notes = "Withdraw money from an existing bank account. " +
            "You cannot withdraw from an account which does not exist")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Withdraw money successful"),
            @ApiResponse(code = 404, message = "Account does not exist to perform withdrawal"),
            @ApiResponse(code = 400, message = "Invalid request received"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response withdrawMonies(@ApiParam(value="account to update", name="account") AccountTransactionData data){

        WithdrawMoneyCommand withdrawCommand = new WithdrawMoneyCommand(data.getAccountId(), data.getAmount());
        config.commandBus().dispatch(asCommandMessage(withdrawCommand));
        return Response.ok().build();
    }

    @POST
    @Path("/deposit")
    @ApiOperation(value = "Deposit money to an existing bank account", notes = "Deposit money to an existing bank account. " +
            "You cannot deposit to an account which does not exist")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deposit money successful"),
            @ApiResponse(code = 404, message = "Account does not exist to perform deposit"),
            @ApiResponse(code = 400, message = "Invalid request received"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Consumes(MediaType.APPLICATION_JSON)
    public Response depositMoney(@ApiParam(value="account to update", name="account") AccountTransactionData data){

        DepositMoneyCommand depositCommand = new DepositMoneyCommand(data.getAccountId(), data.getAmount());
        config.commandBus().dispatch(asCommandMessage(depositCommand));
        return Response.ok().build();
    }
}