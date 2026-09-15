package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Vergleicht zwei Adressen.
 *
 * <p>Ohne Angabe gilt die Standardreihenfolge Nachname -> Vorname -> Registrierungsdatum
 * (Aufgabe 1). Ueber {@link SortField} und {@link SortDirection} kann nach beliebigen
 * Attributen und in beide Richtungen sortiert werden (Aufgabe 2).</p>
 *
 * <p>Strings werden natuerlich verglichen (Gross- vor Kleinbuchstaben), {@code null}-Werte
 * landen immer am Ende, unabhaengig von der Sortierrichtung des Feldes.</p>
 */
public class AddressComparator implements Comparator<Address> {

    /** Standardreihenfolge aus Aufgabe 1. */
    public static final List<SortField> DEFAULT_ORDER =
            List.of(SortField.LASTNAME, SortField.FIRSTNAME, SortField.REGISTRATION_DATE);

    private final List<SortField> sortFields;
    private final SortDirection direction;
    private final Comparator<Address> delegate;

    /** Vergleicht nach Nachname, dann Vorname, dann Registrierungsdatum (aufsteigend). */
    public AddressComparator() {
        this(SortDirection.ASC, DEFAULT_ORDER);
    }

    /** Vergleicht aufsteigend nach den angegebenen Feldern (in dieser Reihenfolge). */
    public AddressComparator(SortField... sortFields) {
        this(SortDirection.ASC, Arrays.asList(sortFields));
    }

    /** Vergleicht in der angegebenen Richtung nach den angegebenen Feldern. */
    public AddressComparator(SortDirection direction, SortField... sortFields) {
        this(direction, Arrays.asList(sortFields));
    }

    private AddressComparator(SortDirection direction, List<SortField> sortFields) {
        if (direction == null) {
            throw new IllegalArgumentException("direction darf nicht null sein");
        }
        if (sortFields == null || sortFields.isEmpty()) {
            throw new IllegalArgumentException("mindestens ein sortField wird benoetigt");
        }
        if (sortFields.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("sortField darf nicht null sein");
        }
        this.direction = direction;
        this.sortFields = List.copyOf(sortFields);
        this.delegate = buildDelegate(direction, this.sortFields);
    }

    private static Comparator<Address> buildDelegate(SortDirection direction, List<SortField> sortFields) {
        Comparator<Address> combined = null;
        for (SortField field : sortFields) {
            Comparator<Address> next = field.comparator(direction);
            combined = combined == null ? next : combined.thenComparing(next);
        }
        return combined;
    }

    @Override
    public int compare(Address a1, Address a2) {
        if (a1 == a2) {
            return 0;
        }
        if (a1 == null) {
            return 1;
        }
        if (a2 == null) {
            return -1;
        }
        return delegate.compare(a1, a2);
    }

    public List<SortField> getSortFields() {
        return sortFields;
    }

    public SortDirection getDirection() {
        return direction;
    }

    /** Sortierrichtung. */
    public enum SortDirection {
        ASC,
        DESC
    }

    /** Attribute einer Adresse, nach denen sortiert werden kann. */
    public enum SortField {
        ID(nullSafe(Address::getId)),
        FIRSTNAME(nullSafe(Address::getFirstname)),
        LASTNAME(nullSafe(Address::getLastname)),
        PHONENUMBER(nullSafe(Address::getPhonenumber)),
        REGISTRATION_DATE(nullSafe(Address::getRegistrationDate));

        private final Directions directions;

        SortField(Directions directions) {
            this.directions = directions;
        }

        Comparator<Address> comparator(SortDirection direction) {
            return direction == SortDirection.DESC ? directions.desc() : directions.asc();
        }

        /**
         * Baut den Vergleich fuer beide Richtungen. Fehlende Werte landen in beiden
         * Richtungen am Ende, damit eine leere Adresse eine Liste nie anfuehrt.
         */
        private static <U extends Comparable<? super U>> Directions nullSafe(
                Function<Address, U> attribute) {
            return new Directions(
                    Comparator.comparing(attribute, Comparator.nullsLast(Comparator.naturalOrder())),
                    Comparator.comparing(attribute, Comparator.nullsLast(Comparator.reverseOrder())));
        }

        /** Der gleiche Vergleich einmal aufsteigend und einmal absteigend. */
        private record Directions(Comparator<Address> asc, Comparator<Address> desc) {
        }
    }
}
