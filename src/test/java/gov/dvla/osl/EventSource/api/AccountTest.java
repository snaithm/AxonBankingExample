package gov.dvla.osl.EventSource.api;

import gov.dvla.osl.EventSource.core.*;
import gov.dvla.osl.EventSource.exceptions.OverdraftLimitExceededException;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {

    private FixtureConfiguration<Account> fixture;

    @Before
    public void setUp() throws Exception {

        fixture = new AggregateTestFixture<>(Account.class);
    }

    @Test
    public void testCreateAccount() throws Exception {

        fixture.givenNoPriorActivity()
                .when(new CreateAccountCommand("1234", 1000))
                .expectEvents(new AccountCreatedEvent("1234", 1000));
    }

    @Test
    public void testWithdrawReasonableAmount() {

        fixture.given(new AccountCreatedEvent("1234", 1000))
                .when(new WithdrawMoneyCommand("1234", 600))
                .expectEvents(new MoneyWithdrawnEvent("1234", 600, -600));
    }

    @Test
    public void testWithdrawAbsurdAmount() {

        fixture.given(new AccountCreatedEvent("1234", 1000))
                .when(new WithdrawMoneyCommand("1234", 1001))
                .expectNoEvents()
                .expectException(OverdraftLimitExceededException.class);
    }

    @Test
    public void testWithdrawTwice() {

        fixture.given(new AccountCreatedEvent("1234", 1000),
                      new MoneyWithdrawnEvent("1234", 300, -300)  )
                .when(new WithdrawMoneyCommand("1234", 100))
                .expectEvents(new MoneyWithdrawnEvent("1234", 100,-400));
    }

    @Test
    public void testDepositOneAmount() {

        fixture.given(new AccountCreatedEvent("1233", 0))
                .when(new DepositMoneyCommand("1233", 600))
                .expectEvents(new MoneyDepositedEvent("1233", 600, 600));
    }

    @Test
    public void testDepositTwoAmounts() {

        fixture.given(new AccountCreatedEvent("1235", 0),
                new MoneyDepositedEvent("1235", 300, 300)  )
                .when(new DepositMoneyCommand("1235", 100))
                .expectEvents(new MoneyDepositedEvent("1235", 100,400));
    }
}