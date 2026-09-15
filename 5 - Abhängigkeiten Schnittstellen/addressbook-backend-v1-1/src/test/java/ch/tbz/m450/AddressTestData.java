package ch.tbz.m450;

import ch.tbz.m450.repository.Address;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Kleine Fabrik fuer Testadressen, damit in den Tests nur noch das steht,
 * was fuer den jeweiligen Testfall wirklich relevant ist.
 */
public final class AddressTestData {

    private AddressTestData() {
    }

    /** Adresse mit allen Attributen. */
    public static Address address(int id, String firstname, String lastname, String phonenumber, Date registrationDate) {
        return new Address(id, firstname, lastname, phonenumber, registrationDate);
    }

    /** Adresse, bei der nur der Name eine Rolle spielt. */
    public static Address named(int id, String firstname, String lastname) {
        return address(id, firstname, lastname, "+41 44 000 00 00", date(2024, 1, 1));
    }

    /** Datum ohne Uhrzeit, damit Vergleiche nachvollziehbar bleiben. */
    public static Date date(int year, int month, int day) {
        return Date.from(LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
