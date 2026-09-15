# Aufgabe 2

Der Comparator kann jetzt nach jedem Attribut einer Adresse und in beide Richtungen sortieren.
Starten und Testen wie in [UEBUNG-1.md](UEBUNG-1.md) beschrieben.

## Erweiterung

[AddressComparator.java](addressbook-backend-v1-1/src/main/java/ch/tbz/m450/util/AddressComparator.java) hat zwei Enums
bekommen:

```java
public enum SortField { ID, FIRSTNAME, LASTNAME, PHONENUMBER, REGISTRATION_DATE }
public enum SortDirection { ASC, DESC }
```

| Aufruf | Wirkung |
| --- | --- |
| `new AddressComparator()` | Standard aus Aufgabe 1: Nachname, Vorname, Datum, aufsteigend |
| `new AddressComparator(FIRSTNAME)` | aufsteigend nach Vorname |
| `new AddressComparator(REGISTRATION_DATE, LASTNAME)` | zuerst Datum, bei Gleichstand Nachname |
| `new AddressComparator(DESC, PHONENUMBER)` | absteigend nach Telefonnummer |

Der parameterlose Konstruktor bleibt erhalten, damit `AddressService.getAll()` unverändert weiterläuft. Die Reihenfolge
der Felder im Aufruf ist die Auswertungsreihenfolge.

### Entscheide

- **Ein Enum statt fünf Comparator-Klassen.** Jedes `SortField` bringt seinen Vergleich selber mit, `AddressComparator`
  kettet sie mit `thenComparing()`.
- **Fehlende Werte bleiben auch bei `DESC` am Ende.** Ein einfaches `reversed()` würde `null` nach vorne drehen, darum
  hält jedes `SortField` den Vergleich zweimal: `nullsLast(naturalOrder())` und `nullsLast(reverseOrder())`.
- **Ungültige Aufrufe fliegen im Konstruktor**, nicht erst beim Sortieren: leere Feldliste, `null` als Feld oder
  Richtung → `IllegalArgumentException`.
- **`getSortFields()` gibt eine unveränderliche Liste zurück.**

## Tests

19 Tests in [AddressComparatorSortFieldTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/util/AddressComparatorSortFieldTest.java).

Die drei Basisadressen im `@BeforeEach` sind so gebaut, dass **jedes Attribut eine andere Reihenfolge** ergibt. Ein Test
kann also nicht zufällig grün sein:

| | Id | Vorname | Nachname | Telefon | Registriert |
| --- | --- | --- | --- | --- | --- |
| anna | 3 | Anna | Zueger | ...300 30 30 | 01.06.2025 |
| beat | 1 | Beat | Meier | ...100 10 10 | 14.02.2023 |
| carla | 2 | Carla | Ammann | ...200 20 20 | 30.09.2024 |

| Testfall | Erwartete Reihenfolge |
| --- | --- |
| `SortField.ID` | beat, carla, anna |
| `SortField.FIRSTNAME` | anna, beat, carla |
| `SortField.LASTNAME` | carla, beat, anna |
| `SortField.PHONENUMBER` | beat, carla, anna |
| `SortField.REGISTRATION_DATE` | beat, carla, anna |
| `DESC` + `FIRSTNAME` | carla, beat, anna |
| `REGISTRATION_DATE, LASTNAME` mit zwei Adressen am 30.09.2024 | beat, abtBeat, carla, anna |
| `DESC` + `PHONENUMBER`, eine Adresse ohne Telefon | anna, carla, beat, ohneTelefon |

Dazu zwei parametrisierte Tests, die jedes neue `SortField` automatisch mittesten:

- `@EnumSource(SortField.class)` — jedes Feld erkennt Gleichheit (`compare(a,a) == 0`) und ist antisymmetrisch
- `@EnumSource(SortDirection.class)` — beide Richtungen werden korrekt gemerkt

Und die Fehlerfälle: leere Feldliste, `null` als Feld, `null` als Richtung, Unveränderlichkeit der Feldliste.

## Was nicht angepasst wurde

`AddressService.getAll()` sortiert weiter mit der Standardreihenfolge. Die Sortierung über die REST-Schnittstelle
wählbar zu machen (`GET /address?sort=firstname&dir=desc`) war nicht Teil der Aufgabe. Der Comparator wäre dafür fertig,
es fehlt nur das Durchreichen von Controller zu Service.
