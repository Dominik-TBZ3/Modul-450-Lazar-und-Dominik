package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.tbz.m450.AddressTestData.address;
import static ch.tbz.m450.AddressTestData.date;
import static ch.tbz.m450.AddressTestData.named;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Aufgabe 1: Standardreihenfolge Nachname -> Vorname -> Registrierungsdatum.
 *
 * <p>Die Vorgabe gab immer -1 zurueck. Genau das deckt
 * {@link #vergleichIstAntisymmetrisch()} ab, denn eine Konstante kann die
 * Antisymmetrie nie erfuellen.</p>
 */
class AddressComparatorTest {

    private AddressComparator comparator;
    private Address meierAnna;
    private Address meierZoe;
    private Address zuegerAnna;

    @BeforeEach
    void setUp() {
        comparator = new AddressComparator();
        meierAnna = named(1, "Anna", "Meier");
        meierZoe = named(2, "Zoe", "Meier");
        zuegerAnna = named(3, "Anna", "Zueger");
    }

    @Test
    @DisplayName("Dieselbe Adresse ist gleich")
    void identischeReferenzIstGleich() {
        assertThat(comparator.compare(meierAnna, meierAnna)).isZero();
    }

    @Test
    @DisplayName("Zwei inhaltlich gleiche Adressen sind gleich")
    void inhaltlichGleicheAdressenSindGleich() {
        Address kopie = named(99, "Anna", "Meier");

        assertThat(comparator.compare(meierAnna, kopie)).isZero();
    }

    @Test
    @DisplayName("Der Nachname entscheidet zuerst")
    void nachnameEntscheidetZuerst() {
        assertThat(comparator.compare(meierAnna, zuegerAnna)).isNegative();
        assertThat(comparator.compare(zuegerAnna, meierAnna)).isPositive();
    }

    @Test
    @DisplayName("Bei gleichem Nachnamen entscheidet der Vorname")
    void vornameEntscheidetAlsZweites() {
        assertThat(comparator.compare(meierAnna, meierZoe)).isNegative();
        assertThat(comparator.compare(meierZoe, meierAnna)).isPositive();
    }

    @Test
    @DisplayName("Bei gleichem Namen entscheidet das Registrierungsdatum")
    void datumEntscheidetAlsDrittes() {
        Address frueh = address(1, "Anna", "Meier", "+41 44 111 11 11", date(2023, 1, 1));
        Address spaet = address(2, "Anna", "Meier", "+41 44 222 22 22", date(2025, 12, 31));

        assertThat(comparator.compare(frueh, spaet)).isNegative();
        assertThat(comparator.compare(spaet, frueh)).isPositive();
    }

    @Test
    @DisplayName("Der Vorname wird erst nach dem Nachnamen beachtet")
    void nachnameSchlaegtVorname() {
        Address zuegerAaron = named(4, "Aaron", "Zueger");

        assertThat(comparator.compare(meierZoe, zuegerAaron)).isNegative();
    }

    @Test
    @DisplayName("Eine ganze Liste wird korrekt sortiert")
    void listeWirdKorrektSortiert() {
        Address meierAnna2023 = address(4, "Anna", "Meier", "+41 44 333 33 33", date(2023, 5, 5));
        List<Address> addresses = new ArrayList<>(
                List.of(zuegerAnna, meierZoe, meierAnna, meierAnna2023));

        addresses.sort(comparator);

        assertThat(addresses)
                .extracting(Address::getLastname, Address::getFirstname, Address::getRegistrationDate)
                .containsExactly(
                        tuple("Meier", "Anna", date(2023, 5, 5)),
                        tuple("Meier", "Anna", date(2024, 1, 1)),
                        tuple("Meier", "Zoe", date(2024, 1, 1)),
                        tuple("Zueger", "Anna", date(2024, 1, 1)));
    }

    @Test
    @DisplayName("Der Vergleich ist antisymmetrisch (die Vorgabe mit dem festen -1 faellt hier durch)")
    void vergleichIstAntisymmetrisch() {
        int hin = comparator.compare(meierAnna, zuegerAnna);
        int zurueck = comparator.compare(zuegerAnna, meierAnna);

        assertThat(Integer.signum(hin)).isEqualTo(-Integer.signum(zurueck));
    }

    @Test
    @DisplayName("Sortieren ist idempotent, ein zweiter Durchlauf aendert nichts")
    void sortierenIstIdempotent() {
        List<Address> addresses = new ArrayList<>(List.of(zuegerAnna, meierZoe, meierAnna));

        addresses.sort(comparator);
        List<Address> nachErstemLauf = List.copyOf(addresses);
        addresses.sort(comparator);

        assertThat(addresses).containsExactlyElementsOf(nachErstemLauf);
    }

    @Test
    @DisplayName("Adressen ohne Nachnamen landen am Ende")
    void fehlenderNachnameLandetAmEnde() {
        Address ohneNachname = address(5, "Anna", null, "+41 44 444 44 44", date(2024, 1, 1));
        List<Address> addresses = new ArrayList<>(List.of(ohneNachname, zuegerAnna, meierAnna));

        addresses.sort(comparator);

        assertThat(addresses).containsExactly(meierAnna, zuegerAnna, ohneNachname);
    }

    @Test
    @DisplayName("Ein fehlendes Registrierungsdatum landet am Ende")
    void fehlendesDatumLandetAmEnde() {
        Address ohneDatum = address(6, "Anna", "Meier", "+41 44 555 55 55", null);

        assertThat(comparator.compare(ohneDatum, meierAnna)).isPositive();
        assertThat(comparator.compare(meierAnna, ohneDatum)).isNegative();
    }

    @Test
    @DisplayName("null-Adressen werfen nicht, sondern landen am Ende")
    void nullAdressenLandenAmEnde() {
        List<Address> addresses = new ArrayList<>(Arrays.asList(null, zuegerAnna, null, meierAnna));

        addresses.sort(comparator);

        assertThat(addresses).containsExactly(meierAnna, zuegerAnna, null, null);
        assertThat(comparator.compare(null, null)).isZero();
    }

    @Test
    @DisplayName("Standardmaessig gilt Nachname, Vorname, Registrierungsdatum aufsteigend")
    void standardreihenfolgeIstDokumentiert() {
        assertThat(comparator.getSortFields()).containsExactly(
                AddressComparator.SortField.LASTNAME,
                AddressComparator.SortField.FIRSTNAME,
                AddressComparator.SortField.REGISTRATION_DATE);
        assertThat(comparator.getDirection()).isEqualTo(AddressComparator.SortDirection.ASC);
    }

    @Test
    @DisplayName("Spy: sort() ruft compare() tatsaechlich auf")
    void spyProtokolliertDieAufrufe() {
        AddressComparator spy = spy(new AddressComparator());
        List<Address> addresses = new ArrayList<>(List.of(zuegerAnna, meierAnna));

        addresses.sort(spy);

        verify(spy, atLeastOnce()).compare(any(Address.class), any(Address.class));
        assertThat(addresses).containsExactly(meierAnna, zuegerAnna);
    }
}
