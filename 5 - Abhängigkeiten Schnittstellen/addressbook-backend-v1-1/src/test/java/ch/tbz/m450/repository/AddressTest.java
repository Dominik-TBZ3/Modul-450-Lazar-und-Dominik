package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static ch.tbz.m450.AddressTestData.date;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests fuer die Entity. Geprueft wird, dass die von Lombok generierten
 * Konstruktoren, Getter und Setter tatsaechlich vorhanden sind und die Werte
 * unveraendert durchreichen.
 */
class AddressTest {

    private Address address;
    private Date registrationDate;

    @BeforeEach
    void setUp() {
        registrationDate = date(2024, 3, 15);
        address = new Address();
    }

    @Test
    @DisplayName("Der leere Konstruktor liefert eine Adresse mit Defaultwerten")
    void leererKonstruktorSetztDefaults() {
        assertThat(address.getId()).isZero();
        assertThat(address.getFirstname()).isNull();
        assertThat(address.getLastname()).isNull();
        assertThat(address.getPhonenumber()).isNull();
        assertThat(address.getRegistrationDate()).isNull();
    }

    @Test
    @DisplayName("Die Setter schreiben alle Attribute")
    void setterSchreibenAlleAttribute() {
        address.setId(42);
        address.setFirstname("Anna");
        address.setLastname("Meier");
        address.setPhonenumber("+41 44 111 22 33");
        address.setRegistrationDate(registrationDate);

        assertThat(address.getId()).isEqualTo(42);
        assertThat(address.getFirstname()).isEqualTo("Anna");
        assertThat(address.getLastname()).isEqualTo("Meier");
        assertThat(address.getPhonenumber()).isEqualTo("+41 44 111 22 33");
        assertThat(address.getRegistrationDate()).isEqualTo(registrationDate);
    }

    @Test
    @DisplayName("Der Konstruktor mit allen Argumenten uebernimmt die Werte in der richtigen Reihenfolge")
    void vollstaendigerKonstruktorUebernimmtWerte() {
        Address created = new Address(7, "Beat", "Zueger", "+41 79 999 88 77", registrationDate);

        assertThat(created.getId()).isEqualTo(7);
        assertThat(created.getFirstname()).isEqualTo("Beat");
        assertThat(created.getLastname()).isEqualTo("Zueger");
        assertThat(created.getPhonenumber()).isEqualTo("+41 79 999 88 77");
        assertThat(created.getRegistrationDate()).isEqualTo(registrationDate);
    }

    @Test
    @DisplayName("Attribute lassen sich ueberschreiben")
    void attributeLassenSichUeberschreiben() {
        address.setLastname("Meier");
        address.setLastname("Mueller");

        assertThat(address.getLastname()).isEqualTo("Mueller");
    }

    @Test
    @DisplayName("Fehlende Angaben bleiben null und werfen nicht")
    void fehlendeAngabenBleibenNull() {
        Address created = new Address(1, null, null, null, null);

        assertThat(created.getFirstname()).isNull();
        assertThat(created.getRegistrationDate()).isNull();
    }
}
