# Aufgabe 1

Projekt: [addressbook-backend-v1-1](addressbook-backend-v1-1)

## Starten

Terminal im Ordner `addressbook-backend-v1-1`:

```
.\mvnw.cmd test              # Tests
.\mvnw.cmd spring-boot:run   # Server auf http://localhost:8080
```

Unter Linux/Mac `./mvnw`. Beenden mit `Strg + C`.

## Was wir gemacht haben

* **Tests für alle Klassen geschrieben** — 54 Tests in 6 Klassen, alle grün:
  Entity, Comparator, Service, Controller und Spring-Kontext.
* **`@BeforeEach` überall verwendet**, dazu `@DisplayName`, `@ParameterizedTest`, `@EnumSource`,
  `@Mock`, `@InjectMocks` und `@Captor`.
* **H2 weggemockt**: Der Service wird mit einem Mockito-Mock des Repositories getestet, dadurch
  startet weder Spring noch eine Datenbank. Verwendete Test Doubles: Mock, Stub, Spy, Fake und ArgumentCaptor.
* **Comparator korrekt implementiert**: Die Vorgabe gab immer `-1` zurück und sortierte damit gar nicht.
  Jetzt wird nach Nachname → Vorname → Registrierungsdatum sortiert, leere Werte landen am Ende.

## Gegenprobe

Mit dem alten `return -1` fallen 19 der 54 Tests durch. Die Tests hätten den Fehler also gefunden.
