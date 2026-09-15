# 4 - Unit-Tests

Lazar & Dominik, Modul 450

| Aufgabe | Abgabe |
|---------|--------|
| 1 - Simpler Rechner | [calculator/](calculator/) |
| 2 - JUnit Zusammenfassung | [AUFGABE-2.md](AUFGABE-2.md) |
| 3 - Banken Simulation dokumentiert | [AUFGABE-3.md](AUFGABE-3.md) |
| 4 - Unit-Tests implementiert | [bank/](bank/) |

## Aufgabe 1: calculator

Maven-Projekt, [Calculator.java](calculator/src/main/java/Calculator.java) und
[CalculatorTest.java](calculator/src/test/java/CalculatorTest.java), 12 Tests (3 pro Operation).

Das `pom.xml` ist da, damit die Entwicklungsumgebung JUnit 5 selber auflöst. Ohne Projektdatei
kennt der Editor die Annotationen `@Test` und `@BeforeEach` nicht und meldet jede Testmethode
als "never used".

Solange Maven nicht installiert ist, laufen die Tests im Ordner `calculator` mit javac und java:

```powershell
javac -cp junit-platform-console-standalone-1.10.2.jar -d out src\main\java\Calculator.java src\test\java\CalculatorTest.java
```

```powershell
java -jar junit-platform-console-standalone-1.10.2.jar execute -cp out -c CalculatorTest
```

Alle Pfade müssen relativ bleiben - ein absoluter Pfad enthält das Semikolon des übergeordneten
Ordners, das Java unter Windows als Classpath-Trennzeichen liest.

Die Jar dazu einmalig holen (sie ist in `.gitignore`, liegt also nicht im Repository):

```powershell
curl.exe -O https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar
```

## Aufgabe 4: bank

Maven-Projekt mit JUnit 5 (Jupiter 5.10.2) und JaCoCo, 23 Tests.

```bash
cd "4 - Unit-Tests/bank"
mvn clean test
```

Coverage danach unter `bank/target/site/jacoco/index.html`: 100 % Branches, 99 % Instructions.

Voraussetzungen: JDK 17 oder neuer und Maven 3.8 oder neuer. **Der Pfad darf kein Semikolon
enthalten.** Solange das Repository unter `Modul 450; Applikationen Testen` liegt, bricht
`mvn test` beim Testcompile mit `Symbol nicht gefunden: Klasse Account` ab - Maven gibt javac
absolute Pfade, und das Semikolon ist unter Windows das Classpath-Trennzeichen. Den Ordner also
umbenennen, z.B. auf `Modul 450 - Applikationen Testen`.
