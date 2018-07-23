package fr.jpa.bankaccount.account;

import java.math.BigDecimal;
import java.util.List;

import fr.jpa.bankaccount.bean.AccountStatement;
import fr.jpa.bankaccount.bean.Operation;
import fr.jpa.bankaccount.date.DateProvider;

/**
 * Bank accounts management service.
 *
 * @author jpauchet
 */
public class AccountService {

    /** Accounts storage. */
    private final AccountRepository accountRepository;

    /** Date provider. */
    private final DateProvider dateProvider;

    /**
     * Constructor.
     *
     * @param dateProvider
     *            the date provider
     */
    public AccountService(final AccountRepository accountRepository, final DateProvider dateProvider) {
        this.accountRepository = accountRepository;
        this.dateProvider = dateProvider;
    }

    /**
     * Performs a money deposit on the account.
     *
     * @param accountNumber
     *            the account number
     * @param amount
     *            the amount do deposit
     */
    public void depositMoney(final String accountNumber, final BigDecimal amount) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("The account number should be specified");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount for the deposit must be positive");
        }
        final Operation operation = new Operation(accountNumber, OperationType.DEPOSIT, this.dateProvider.getDate(), BigDecimal.ZERO, amount);
        this.accountRepository.create(operation);
    }

    /**
     * Performs a money withdrawal from the account.
     *
     * @param accountNumber
     *            the account number
     * @param amount
     *            the amount to withdraw
     * @return a booleen that tells if the balance is positive or not
     */
    public boolean withdrawMoney(final String accountNumber, final BigDecimal amount) {
        if (accountNumber == null) {
            throw new IllegalArgumentException("The account number should be specified");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The amount for the withdrawal must be positive");
        }
        final Operation operation = new Operation(accountNumber, OperationType.WITHDRAWAL, this.dateProvider.getDate(), amount, BigDecimal.ZERO);
        this.accountRepository.create(operation);
        return this.accountRepository.calculateBalance(accountNumber).compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * Establishes the account statement of the account.
     *
     * @param accountNumber
     *            the account number
     * @return the account statement of the account
     */
    public AccountStatement establishAccountStatement(final String accountNumber) {
        final List<Operation> operations = this.accountRepository.list(accountNumber);
        final BigDecimal balance = this.accountRepository.calculateBalance(accountNumber);
        return new AccountStatement(operations, balance);
    }

    /**
     * Gets the account repository.
     *
     * @return the account repository
     */
    public AccountRepository getAccountRepository() {
        return this.accountRepository;
    }

}
