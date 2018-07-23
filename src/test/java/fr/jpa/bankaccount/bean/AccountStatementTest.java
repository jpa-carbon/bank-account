package fr.jpa.bankaccount.bean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.jpa.bankaccount.account.OperationType;

/**
 * Tests {@link AccountStatement}.
 *
 * @author $Author: jpauchet $
 * @version $Revision: 0 $
 */
public class AccountStatementTest {

    /**
     * Tests {@link AccountStatement#print()}.
     */
    @Test
    public void testPrint() {
        // prepare
        final List<Operation> operations = Arrays.asList( //
                new Operation("12345", OperationType.DEPOSIT, Instant.parse("2018-03-12T09:30:00.00Z"), BigDecimal.ZERO, new BigDecimal("12.50")),
                new Operation("12345", OperationType.WITHDRAWAL, Instant.parse("2018-03-14T14:17:00.00Z"), new BigDecimal("6.00"), BigDecimal.ZERO),
                new Operation("12345", OperationType.DEPOSIT, Instant.parse("2018-03-13T18:20:00.00Z"), BigDecimal.ZERO, new BigDecimal("50.75")) //
        );
        final AccountStatement accountStatement = new AccountStatement(operations, new BigDecimal("57.25"));

        // call
        final String printed = accountStatement.getPrint();

        // check
        assertThat(printed, notNullValue());
        final StringBuilder expectedPrinted = new StringBuilder();
        expectedPrinted.append("Operation  | Date           | Credit | Debit\r\n");
        expectedPrinted.append("Deposit    | 12/03/18 10:30 |  12.50 |      0\r\n");
        expectedPrinted.append("Withdrawal | 14/03/18 15:17 |      0 |   6.00\r\n");
        expectedPrinted.append("Deposit    | 13/03/18 19:20 |  50.75 |      0\r\n");
        expectedPrinted.append("\r\n");
        expectedPrinted.append("Balance\r\n");
        expectedPrinted.append("57.25\r\n");
        assertThat(printed, equalTo(expectedPrinted.toString()));
    }

}
