package fr.jpa.bankaccount.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fr.jpa.bankaccount.bean.Operation;

/**
 * Tests {@link AccountRepository}.
 *
 * @author $Author: jpauchet $
 * @version $Revision: 0 $
 */
public class AccountRepositoryTest {

    /** Stockage des comptes bancaires. */
    private AccountRepository accountRepository;

    /**
     * Prepares the unit tests execution.
     */
    @Before
    public void setUp() throws Exception {
        final Map<String, List<Operation>> accounts = new HashMap<>();
        accounts.put("12345", //
                Arrays.asList( //
                        new Operation("12345", OperationType.DEPOSIT, Instant.parse("2018-03-12T00:00:00.00Z"), BigDecimal.ZERO, new BigDecimal("12.50")),
                        new Operation("12345", OperationType.WITHDRAWAL, Instant.parse("2018-03-14T00:00:00.00Z"), new BigDecimal("6.00"), BigDecimal.ZERO),
                        new Operation("12345", OperationType.DEPOSIT, Instant.parse("2018-03-13T00:00:00.00Z"), BigDecimal.ZERO, new BigDecimal("50.75")) //
                ) //
        );
        accounts.put("424242", //
                Arrays.asList( //
                        new Operation("424242", OperationType.DEPOSIT, Instant.parse("2018-03-09T00:00:00.00Z"), BigDecimal.ZERO, new BigDecimal("100.00")) //
                ) //
        );
        this.accountRepository = new AccountRepository(accounts);
    }

    /**
     * Tests {@link AccountRepository#list(String)}.
     */
    @Test
    public void testList() {
        // call
        final List<Operation> operations = this.accountRepository.list("12345");

        // check
        assertThat(operations, notNullValue());
        assertThat(operations, hasSize(3));
        assertThat(operations.get(0),
                allOf(Arrays.asList( //
                        hasProperty("accountNumber", equalTo("12345")), //
                        hasProperty("operationType", equalTo(OperationType.WITHDRAWAL)), //
                        hasProperty("operationDate", equalTo(Instant.parse("2018-03-14T00:00:00.00Z"))), //
                        hasProperty("debit", equalTo(new BigDecimal("6.00"))), //
                        hasProperty("credit", equalTo(BigDecimal.ZERO)) //
                )) //
        );
        assertThat(operations.get(1),
                allOf(Arrays.asList( //
                        hasProperty("accountNumber", equalTo("12345")), //
                        hasProperty("operationType", equalTo(OperationType.DEPOSIT)), //
                        hasProperty("operationDate", equalTo(Instant.parse("2018-03-13T00:00:00.00Z"))), //
                        hasProperty("debit", equalTo(BigDecimal.ZERO)), //
                        hasProperty("credit", equalTo(new BigDecimal("50.75"))) //
                )) //
        );
    }

    /**
     * Tests {@link AccountRepository#create(Operation)}.
     */
    @Test
    public void testCreateDeposit() {
        // prepare
        final String accountNumber = "67890";
        List<Operation> operations = this.accountRepository.list(accountNumber);
        assertThat(operations, notNullValue());
        assertThat(operations, hasSize(0));

        // call
        final Operation operation = new Operation(accountNumber, OperationType.DEPOSIT, Instant.parse("2018-03-14T00:00:00.00Z"), null, new BigDecimal("42.05"));
        this.accountRepository.create(operation);

        // check
        operations = this.accountRepository.list(accountNumber);
        assertThat(operations, notNullValue());
        assertThat(operations, hasSize(1));
        assertThat(operations.get(0),
                allOf( //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(operation.getOperationType())), //
                        hasProperty("operationDate", equalTo(operation.getOperationDate())), //
                        hasProperty("debit", equalTo(operation.getDebit())), //
                        hasProperty("credit", equalTo(operation.getCredit())) //
                ) //
        );
    }

    /**
     * Tests {@link AccountRepository#create(Operation)}.
     */
    @Test
    public void testCreateWithdrawal() {
        // prepare
        final String accountNumber = "67890";
        List<Operation> operations = this.accountRepository.list(accountNumber);
        assertThat(operations, notNullValue());
        assertThat(operations, hasSize(0));

        // call
        final Operation operation = new Operation(accountNumber, OperationType.WITHDRAWAL, Instant.parse("2018-03-14T00:00:00.00Z"), new BigDecimal("42.05"), null);
        this.accountRepository.create(operation);

        // check
        operations = this.accountRepository.list(accountNumber);
        assertThat(operations, notNullValue());
        assertThat(operations, hasSize(1));
        assertThat(operations.get(0),
                allOf( //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(operation.getOperationType())), //
                        hasProperty("operationDate", equalTo(operation.getOperationDate())), //
                        hasProperty("debit", equalTo(operation.getDebit())), //
                        hasProperty("credit", equalTo(operation.getCredit())) //
                ) //
        );
    }

    /**
     * Tests {@link AccountRepository#calculateBalance(String)}.
     */
    @Test
    public void testCalculateBalance() {
        // call
        final BigDecimal balance = this.accountRepository.calculateBalance("12345");

        // check
        assertThat(balance, notNullValue());
        assertThat(balance, equalTo(new BigDecimal("57.25")));
    }

    /**
     * Tests {@link AccountRepository#calculateBalance(String)}.
     */
    @Test
    public void testCalculateBalanceNoOperation() {
        // call
        final BigDecimal balance = this.accountRepository.calculateBalance("434343");

        // check
        assertThat(balance, notNullValue());
        assertThat(balance, equalTo(BigDecimal.ZERO));
    }

}
