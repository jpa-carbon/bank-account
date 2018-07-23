package fr.jpa.bankaccount.account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.jpa.bankaccount.bean.Operation;

/**
 * Bank accounts storage.
 *
 * @author jpauchet
 */
public class AccountRepository {

    /** Bank accounts and their operations. */
    private final Map<String, List<Operation>> accounts;

    /**
     * Constructor.
     */
    public AccountRepository() {
        this.accounts = new HashMap<>();
    }

    /**
     * Constructor.
     *
     * @param accounts
     *            the accounts
     */
    public AccountRepository(final Map<String, List<Operation>> accounts) {
        this.accounts = accounts;
    }

    /**
     * Lists operations on the account.
     *
     * @param accountNumber
     *            the account number
     * @return the list of operations performed on the account
     */
    public List<Operation> list(final String accountNumber) {
        List<Operation> operations = null;
        if (accountNumber != null) {
            operations = this.accounts.get(accountNumber);
        }
        if (operations == null) {
            operations = new ArrayList<>();
        }
        operations.sort(Comparator.comparing(Operation::getOperationDate).reversed());
        return Collections.unmodifiableList(operations);
    }

    /**
     * Creates an operation related to the bank account.
     *
     * @param operation
     *            the operation
     */
    public void create(final Operation operation) {
        if (operation == null || operation.getAccountNumber() == null) {
            return;
        }
        List<Operation> operations = this.accounts.get(operation.getAccountNumber());
        if (operations == null) {
            operations = new ArrayList<>();
            this.accounts.put(operation.getAccountNumber(), operations);
        }
        operations.add(operation);
    }

    /**
     * Calculates the account balance.
     *
     * @param accountNumber
     *            the account number
     * @return the account balance
     */
    public BigDecimal calculateBalance(final String accountNumber) {
        BigDecimal balance = BigDecimal.ZERO;
        for (final Operation operation : this.list(accountNumber)) {
            balance = balance.add(operation.getCredit());
            balance = balance.subtract(operation.getDebit());
        }
        return balance;
    }

}
