package fr.jpa.bankaccount.date;

import java.time.Instant;

/**
 * Date provider.
 *
 * @author jpauchet
 */
public interface DateProvider {

    /**
     * Gets the date.
     *
     * @return the date
     */
    Instant getDate();

}
