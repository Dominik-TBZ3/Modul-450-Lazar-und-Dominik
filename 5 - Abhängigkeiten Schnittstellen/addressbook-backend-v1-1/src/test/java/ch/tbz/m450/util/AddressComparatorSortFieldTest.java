package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.util.AddressComparator.SortDirection;
import ch.tbz.m450.util.AddressComparator.SortField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;

import static ch.tbz.m450.AddressTestData.address;
import static ch.tbz.m450.AddressTestData.date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Aufgabe 2: Der Comparator kann nach beliebigen Attributen und in beide
 * Richtungen sortieren.
 */
class AddressComparatorSortFieldTest {

    private Address anna;
    private Address beat;
    private Address carla;
    private List<Address> addresses;

    @BeforeEach
    void setUp() {
        // Absichtlich so gebaut, dass jedes Attribut eine andere Reihenfolge ergibt.
        anna = address(3, "Anna", "Zueger", "+41 44 300 30 30", date(2025, 6, 1));
        beat = address(1, "Beat", "Meier", "+41 44 100 10 10", date(2023, 2, 14));
        carla = address(2, "Carla", "Ammann", "+41 44 200 20 20", date(2024, 9, 30));
        addresses = new ArrayList<>(List.of(anna, beat, carla));
    }

    @Test
    @DisplayName("Sortiert nach Vorname")
    void sortiertNachVorname() {
        addresses.sort(new AddressComparator(SortField.FIRSTNAME));

        assertThat(addresses).containsExactly(anna, beat, carla);
    }

    @Test
    @DisplayName("Sortiert nach Telefonnummer")
    void sortiertNachTelefonnummer() {
        addresses.sort(new AddressComparator(SortField.PHONENUMBER));

        assertThat(addresses).containsExactly(beat, carla, anna);
    }

    @Test
    @DisplayName("Sortiert nach Registrierungsdatum")
    void sortiertNachRegistrierungsdatum() {
        addresses.sort(new AddressComparator(SortField.REGISTRATION_DATE));

        assertThat(addresses).containsExactly(beat, carla, anna);
    }

    @Test
    @DisplayName("Sortiert nach Id")
    void sortiertNachId() {
        addresses.sort(new AddressComparator(SortField.ID));

        assertThat(addresses).containsExactly(beat, carla, anna);
    }

    @Test
    @DisplayName("Sortiert nach Nachname, unabhaengig von der Standardreihenfolge")
    void sortiertNachNachname() {
        addresses.sort(new AddressComparator(SortField.LASTNAME));

        assertThat(addresses).containsExactly(carla, beat, anna);
    }

    @Test
    @DisplayName("DESC dreht die Reihenfolge um")
    void absteigendDrehtDieReihenfolge() {
        addresses.sort(new AddressComparator(SortDirection.DESC, SortField.FIRSTNAME));

        assertThat(addresses).containsExactly(carla, beat, anna);
    }

    @Test
    @DisplayName("Mehrere Felder werden in der angegebenen Reihenfolge ausgewertet")
    void mehrereFelderWerdenInReihenfolgeAusgewertet() {
        Address abtBeat = address(4, "Beat", "Abt", "+41 44 400 40 40", date(2024, 9, 30));
        addresses.add(abtBeat);

        addresses.sort(new AddressComparator(SortField.REGISTRATION_DATE, SortField.LASTNAME));

        // 2023 zuerst, dann die beiden vom 30.09.2024 nach Nachname (Abt vor Ammann), dann 2025
        assertThat(addresses).containsExactly(beat, abtBeat, carla, anna);
    }

    @Test
    @DisplayName("Das zweite Feld entscheidet nur bei Gleichstand im ersten")
    void zweitesFeldEntscheidetNurBeiGleichstand() {
        Address abtBeat = address(4, "Beat", "Abt", "+41 44 400 40 40", date(2024, 9, 30));
        AddressComparator comparator = new AddressComparator(SortField.REGISTRATION_DATE, SortField.LASTNAME);

        // gleiches Datum -> Nachname entscheidet
        assertThat(comparator.compare(abtBeat, carla)).isNegative();
        // unterschiedliches Datum -> Nachname wird nicht mehr angeschaut
        assertThat(comparator.compare(abtBeat, anna)).isNegative();
    }

    @Test
    @DisplayName("Fehlende Werte bleiben auch bei DESC am Ende")
    void fehlendeWerteBleibenAuchAbsteigendAmEnde() {
        Address ohneTelefon = address(5, "Dora", "Bucher", null, date(2024, 1, 1));
        addresses.add(ohneTelefon);

        addresses.sort(new AddressComparator(SortDirection.DESC, SortField.PHONENUMBER));

        assertThat(addresses).containsExactly(anna, carla, beat, ohneTelefon);
    }

    @ParameterizedTest
    @EnumSource(SortField.class)
    @DisplayName("Jedes Sortierfeld erkennt Gleichheit und ist antisymmetrisch")
    void jedesFeldVerhaeltSichKorrekt(SortField field) {
        AddressComparator comparator = new AddressComparator(field);

        assertThat(comparator.compare(anna, anna)).isZero();
        assertThat(Integer.signum(comparator.compare(anna, beat)))
                .isEqualTo(-Integer.signum(comparator.compare(beat, anna)));
    }

    @ParameterizedTest
    @EnumSource(SortDirection.class)
    @DisplayName("Die gewaehlte Richtung wird gemerkt")
    void richtungWirdGemerkt(SortDirection direction) {
        AddressComparator comparator = new AddressComparator(direction, SortField.ID);

        assertThat(comparator.getDirection()).isEqualTo(direction);
        assertThat(comparator.getSortFields()).containsExactly(SortField.ID);
    }

    @Test
    @DisplayName("Die Feldliste kann von aussen nicht veraendert werden")
    void feldlisteIstUnveraenderlich() {
        AddressComparator comparator = new AddressComparator(SortField.ID);

        assertThat(comparator.getSortFields()).isUnmodifiable();
    }

    @Test
    @DisplayName("Ohne Sortierfeld ist der Comparator nicht sinnvoll und wird abgelehnt")
    void ohneSortierfeldWirdAbgelehnt() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator(new SortField[0]));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator(SortDirection.ASC, new SortField[0]));
    }

    @Test
    @DisplayName("null als Sortierfeld oder Richtung wird abgelehnt")
    void nullWirdAbgelehnt() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator(SortField.ID, null));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new AddressComparator((SortDirection) null, SortField.ID));
    }
}
