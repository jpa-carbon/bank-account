package fr.jpa.bankaccount.bean;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Bank account statement.
 *
 * @author jpauchet
 */
public final class AccountStatement {

    /** Operations on the account. */
    private final List<Operation> operations;

    /** Balance. */
    private final BigDecimal balance;

    /**
     * Constructor.
     *
     * @param operations
     *            the operations
     * @param balance
     *            the balance
     */
    public AccountStatement(final List<Operation> operations, final BigDecimal balance) {
        this.operations = operations;
        this.balance = balance;
    }

    /**
     * Gets the print of the account statement.
     */
    public String getPrint() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT) //
                .withLocale(Locale.FRANCE) //
                .withZone(ZoneId.of("GMT+1"));
        final StringBuilder printed = new StringBuilder();
        printed.append("Operation  | Date           | Credit | Debit\r\n");
        for (final Operation operation : this.operations) {
            printed.append(String.format("%-10s", operation.getOperationType().getLabel()));
            printed.append(" | ");
            printed.append(formatter.format(operation.getOperationDate()));
            printed.append(" | ");
            printed.append(String.format("%6s", operation.getCredit()));
            printed.append(" | ");
            printed.append(String.format("%6s", operation.getDebit()));
            printed.append("\r\n");
        }
        printed.append("\r\n");
        printed.append("Balance\r\n");
        printed.append(this.balance);
        printed.append("\r\n");
        return printed.toString();
    }

    /**
     * Gets the operations.
     *
     * @return the operations
     */
    public List<Operation> getOperations() {
        return Collections.unmodifiableList(this.operations);
    }

    /**
     * Gets the balance.
     *
     * @return the balance
     */
    public BigDecimal getBalance() {
        return this.balance;
    }

}
