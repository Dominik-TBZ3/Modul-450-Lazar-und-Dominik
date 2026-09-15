# Aufgabe 1

Projekt: [addressbook-backend-v1-1](addressbook-backend-v1-1) (Spring Boot 3.5.4, Java 21).

## Programm starten

Terminal im Ordner `5 - Abhängigkeiten Schnittstellen/addressbook-backend-v1-1` öffnen:

```
./mvnw spring-boot:run     # Server starten
./mvnw test                # nur Tests
```

Unter Windows/PowerShell statt `./mvnw` den Befehl `.\mvnw.cmd` verwenden. Beim ersten Start lädt Maven die
Abhängigkeiten herunter, das dauert ein paar Minuten. Läuft der Server, steht in der Konsole:

```
Tomcat started on port 8080 (http) with context path '/'
Started AddressbookApplication in 4.08 seconds
```

Beenden mit `Strg + C`.

### Ausprobieren

Die Daten liegen in einer H2-Datenbank im Arbeitsspeicher und sind nach dem Beenden wieder weg.

| Was | Aufruf |
| --- | --- |
| Adresse anlegen | `curl -X POST http://localhost:8080/address -H "Content-Type: application/json" -d '{"id":1,"firstname":"Beat","lastname":"Meier","phonenumber":"079 100 10 10","registrationDate":"2023-02-14T00:00:00.000+00:00"}'` |
| Alle Adressen (sortiert) | `curl http://localhost:8080/address` |
| Eine Adresse | `curl http://localhost:8080/address/1` |
| H2-Konsole im Browser | <http://localhost:8080/h2-console> (JDBC-URL `jdbc:h2:mem:mydb`, Benutzer `sa`, Passwort `password`) |

Die `id` muss man selber mitgeben, das Feld wird nicht automatisch vergeben.

### Anpassungen an der Vorgabe

Zwei Änderungen waren nötig, damit die Vorgabe auf unserem Rechner (JDK 25) kompiliert:

| Was | Warum |
| --- | --- |
| Lombok 1.18.38 → 1.18.48 | 1.18.38 kennt JDK 25 nicht |
| `annotationProcessorPaths` für Lombok in der `pom.xml` | Ab JDK 23 zieht javac Annotation Processors nicht mehr automatisch vom Classpath, sonst generiert Lombok keine Getter |

## Comparator korrekt implementiert

Die Vorgabe gab immer `-1` zurück, damit sortierte `getAll()` gar nicht. Der ursprüngliche Klassenname
(`LastnameFirstnameRegistrationDatecomparator`) gibt die Reihenfolge vor: **Nachname → Vorname → Registrierungsdatum,
aufsteigend**.

[AddressComparator.java](addressbook-backend-v1-1/src/main/java/ch/tbz/m450/util/AddressComparator.java) baut das mit
`Comparator.comparing(...).thenComparing(...)`. Fehlende Werte (`null`) und `null`-Adressen landen am Ende statt eine
Exception zu werfen.

## Tests

**54 Tests in 6 Klassen, alle grün.**

| Testklasse | Was geprüft wird | Tests |
| --- | --- | --- |
| [AddressTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/repository/AddressTest.java) | Entity: Konstruktoren, Getter, Setter von Lombok | 5 |
| [AddressComparatorTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/util/AddressComparatorTest.java) | Standardreihenfolge, Antisymmetrie, `null`, Liste sortieren | 14 |
| [AddressComparatorSortFieldTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/util/AddressComparatorSortFieldTest.java) | Aufgabe 2, siehe [UEBUNG-2.md](UEBUNG-2.md) | 19 |
| [AddressServiceTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/service/AddressServiceTest.java) | Service mit gemocktem Repository, ohne H2 | 10 |
| [AddressControllerTest](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/controller/AddressControllerTest.java) | Statuscodes 201, 200, 404 mit gemocktem Service | 5 |
| [AddressbookApplicationTests](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/AddressbookApplicationTests.java) | Spring-Kontext startet | 1 |

`@BeforeEach` ist überall im Einsatz, Testdaten und Mocks werden vor jedem Test neu aufgebaut. Dazu `@DisplayName`,
`@ParameterizedTest` mit `@EnumSource`, `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks` und `@Captor`.
Die Testadressen kommen aus [AddressTestData](addressbook-backend-v1-1/src/test/java/ch/tbz/m450/AddressTestData.java).

## H2 weggemockt

Der Service hängt nur am `AddressRepository`. Im Test wird es durch einen Mockito-Mock ersetzt, so startet kein
Spring-Kontext und keine Datenbank:

```java
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock private AddressRepository addressRepository;
    @InjectMocks private AddressService addressService;
```

| Test Double | Wozu |
| --- | --- |
| Mock | Verhalten prüfen: `verify(addressRepository).findAll()`, `never()`, `verifyNoMoreInteractions(...)` |
| Stub | `when(addressRepository.findAll()).thenReturn(...)` liefert feste Daten zum Sortieren |
| Spy | `spy(new AddressComparator())` belegt, dass `List.sort()` wirklich `compare()` aufruft |
| Fake | Repository mit `HashMap` statt H2, über `thenAnswer` gebaut |
| ArgumentCaptor | belegt, dass genau die übergebene Adresse ans Repository geht |

Nur `AddressbookApplicationTests` fasst die echte H2 an, dort geht es ja gerade um die Verdrahtung.

## Gegenprobe

Damit die Tests nicht bloss die eigene Implementation bestätigen, haben wir `compare()` testweise wieder auf `return -1`
gesetzt:

```
Tests run: 54, Failures: 19
```

Durchgefallen sind 7 Tests im `AddressComparatorTest`, 11 im `AddressComparatorSortFieldTest` und 1 im
`AddressServiceTest`. Entity-, Controller- und Kontexttest bleiben grün, was richtig ist, die kennen den Comparator
nicht.
