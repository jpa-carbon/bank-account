package fr.jpa.bankaccount.bean;

import java.math.BigDecimal;
import java.time.Instant;

import fr.jpa.bankaccount.account.OperationType;

/**
 * Operation on a bank account.
 *
 * @author jpauchet
 */
public final class Operation {

    /** Account number. */
    private final String accountNumber;

    /** Operation type. */
    private final OperationType operationType;

    /** Operation date. */
    private final Instant operationDate;

    /** Debit amount. */
    private final BigDecimal debit;

    /** Credit amount. */
    private final BigDecimal credit;

    /**
     * Constructor.
     *
     * @param accountNumber
     *            the account number
     * @param operationType
     *            the operation type
     * @param operationDate
     *            the operation date
     * @param debit
     *            the debit
     * @param credit
     *            the credit
     */
    public Operation(final String accountNumber, final OperationType operationType, final Instant operationDate, final BigDecimal debit, final BigDecimal credit) {
        this.accountNumber = accountNumber;
        this.operationType = operationType;
        this.operationDate = operationDate;
        this.debit = debit;
        this.credit = credit;
    }

    /**
     * Gets the account number.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return this.accountNumber;
    }

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    public OperationType getOperationType() {
        return this.operationType;
    }

    /**
     * Gets the operation date.
     *
     * @return the operation date
     */
    public Instant getOperationDate() {
        return this.operationDate;
    }

    /**
     * Gets the debit.
     *
     * @return the debit
     */
    public BigDecimal getDebit() {
        return this.debit;
    }

    /**
     * Gets the credit.
     *
     * @return the credit
     */
    public BigDecimal getCredit() {
        return this.credit;
    }

}
