package fr.jpa.bankaccount.account;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.jpa.bankaccount.bean.AccountStatement;
import fr.jpa.bankaccount.bean.Operation;
import fr.jpa.bankaccount.date.DateProvider;

/**
 * Tests {@link AccountService}.
 *
 * @author jpauchet
 */
public class AccountServiceTest {

    /** Bank accounts storage. */
    @Mock
    private AccountRepository accountRepository;

    /** Date provider. */
    @Mock
    private DateProvider dateProvider;

    /** Bank accounts management service. */
    @InjectMocks
    private AccountService accountService;

    /**
     * Prepares the unit tests execution.
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests {@link AccountService#depositMoney(String, java.math.BigDecimal)}.
     */
    @Test
    public void testDepositMoney() {
        // prepare
        final Instant date = Instant.parse("2018-03-14T00:00:00.00Z");
        when(this.dateProvider.getDate()).thenReturn(date);

        // call
        final String accountNumber = "12345";
        this.accountService.depositMoney(accountNumber, new BigDecimal("42.00"));

        // check
        final ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
        verify(this.accountRepository).create(operation.capture());
        verify(this.accountRepository, never()).calculateBalance(any(String.class));
        assertThat(operation.getValue(),
                allOf( //
                        notNullValue(), //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(OperationType.DEPOSIT)), //
                        hasProperty("operationDate", equalTo(date)), //
                        hasProperty("credit", equalTo(new BigDecimal("42.00"))) //
                ) //
        );
    }

    /**
     * Tests {@link AccountService#depositMoney(String, java.math.BigDecimal)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDepositMoneyWrongAmount() {
        // call
        this.accountService.depositMoney("12345", new BigDecimal("-42.00"));
    }

    /**
     * Tests {@link AccountService#withdrawMoney(String, java.math.BigDecimal)}.
     */
    @Test
    public void testWithdrawMoneyPositiveBalance() {
        // prepare
        final String accountNumber = "12345";
        when(this.accountRepository.calculateBalance(any(String.class))).thenReturn(new BigDecimal("0.01"));
        final Instant date = Instant.parse("2018-03-14T00:00:00.00Z");
        when(this.dateProvider.getDate()).thenReturn(date);

        // call
        final boolean actual = this.accountService.withdrawMoney(accountNumber, new BigDecimal("49.99"));

        // check
        final ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
        verify(this.accountRepository).create(operation.capture());
        verify(this.accountRepository).calculateBalance(eq(accountNumber));
        assertThat(operation.getValue(),
                allOf( //
                        notNullValue(), //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(OperationType.WITHDRAWAL)), //
                        hasProperty("operationDate", equalTo(date)), //
                        hasProperty("debit", equalTo(new BigDecimal("49.99"))) //
                ) //
        );
        assertThat(actual, equalTo(true));
    }

    /**
     * Tests {@link AccountService#withdrawMoney(String, java.math.BigDecimal)}.
     */
    @Test
    public void testWithdrawMoneyZeroBalance() {
        // prepare
        final String accountNumber = "12345";
        when(this.accountRepository.calculateBalance(any(String.class))).thenReturn(BigDecimal.ZERO);
        final Instant date = Instant.parse("2018-03-14T00:00:00.00Z");
        when(this.dateProvider.getDate()).thenReturn(date);

        // call
        final boolean actual = this.accountService.withdrawMoney(accountNumber, new BigDecimal("50.00"));

        // check
        final ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
        verify(this.accountRepository).create(operation.capture());
        verify(this.accountRepository).calculateBalance(eq(accountNumber));
        assertThat(operation.getValue(),
                allOf( //
                        notNullValue(), //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(OperationType.WITHDRAWAL)), //
                        hasProperty("operationDate", equalTo(date)), //
                        hasProperty("debit", equalTo(new BigDecimal("50.00"))) //
                ) //
        );
        assertThat(actual, equalTo(true));
    }

    /**
     * Tests {@link AccountService#withdrawMoney(String, java.math.BigDecimal)}.
     */
    @Test
    public void testWithdrawMoneyNegativeBalance() {
        // prepare
        final String accountNumber = "12345";
        when(this.accountRepository.calculateBalance(any(String.class))).thenReturn(new BigDecimal("-0.01"));
        final Instant date = Instant.parse("2018-03-14T00:00:00.00Z");
        when(this.dateProvider.getDate()).thenReturn(date);

        // call
        final boolean actual = this.accountService.withdrawMoney(accountNumber, new BigDecimal("50.01"));

        // check
        final ArgumentCaptor<Operation> operation = ArgumentCaptor.forClass(Operation.class);
        verify(this.accountRepository).create(operation.capture());
        verify(this.accountRepository).calculateBalance(eq(accountNumber));
        assertThat(operation.getValue(),
                allOf( //
                        notNullValue(), //
                        hasProperty("accountNumber", equalTo(accountNumber)), //
                        hasProperty("operationType", equalTo(OperationType.WITHDRAWAL)), //
                        hasProperty("operationDate", equalTo(date)), //
                        hasProperty("debit", equalTo(new BigDecimal("50.01"))) //
                ) //
        );
        assertThat(actual, equalTo(false));
    }

    /**
     * Tests {@link AccountService#withdrawMoney(String, java.math.BigDecimal)}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWithdrawMoneyWrongAmount() {
        // call
        this.accountService.withdrawMoney("12345", new BigDecimal("-42.00"));
    }

    /**
     * Tests {@link AccountService#establishAccountStatement(String)}.
     */
    @Test
    public void testEstablishAccountStatement() {
        // prepare
        final String accountNumber = "12345";
        final Operation dummyOperation = new Operation(null, null, null, null, null);
        when(this.accountRepository.list(any(String.class))).thenReturn(Arrays.asList(dummyOperation, dummyOperation, dummyOperation));
        when(this.accountRepository.calculateBalance(any(String.class))).thenReturn(new BigDecimal("42.00"));

        // call
        final AccountStatement actual = this.accountService.establishAccountStatement(accountNumber);

        // check
        verify(this.accountRepository).list(eq(accountNumber));
        verify(this.accountRepository).calculateBalance(eq(accountNumber));
        assertThat(actual.getOperations(), hasSize(3));
        assertThat(actual.getBalance(), equalTo(new BigDecimal("42.00")));
    }

}
