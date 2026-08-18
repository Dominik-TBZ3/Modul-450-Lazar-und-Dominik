# Bank-Software (Übung 3)

Die Bank-Software aus dem M450-Repo, inklusive der JAR-Files, damit sie ohne Downloads läuft.
Original: <https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/tree/main/Unterlagen/teststrategie>

Unsere Testfälle dazu: [../UEBUNG-3.md](../UEBUNG-3.md)

## Starten

Doppelklick auf `start.cmd`. Das Skript stellt die Konsole auf UTF-8, kompiliert beim ersten Aufruf nach `out\`
und startet die App. Nach einer Code-Änderung einfach `out\` löschen, dann wird neu kompiliert.

Von Hand:

```bash
javac -encoding UTF-8 -d out -cp "libs/gson/*;libs/okhttp/*" bank-software-mvn/src/main/java/ch/tbz/bank/software/*.java
java -Dfile.encoding=UTF-8 -Dstdin.encoding=UTF-8 -cp "out;libs/gson/*;libs/okhttp/*" ch.tbz.bank.software.Main
```

## In IntelliJ öffnen

`bank-software-mvn` öffnen (der Ordner mit der `pom.xml`). IntelliJ zieht gson und okhttp selber, die `libs` braucht
man dann nicht. Bei der Run-Konfiguration *Emulate terminal in output console* anhaken, sonst geht die Tastatureingabe
im Run-Fenster nicht.

## Inhalt

| Pfad | Was |
|------|-----|
| `bank-software-mvn/` | Maven-Projekt mit den 5 Java-Klassen |
| `libs/gson`, `libs/okhttp` | die JARs aus `gson.zip` und `okhttp.zip` |
| `start.cmd` | kompiliert und startet |
| `out/` | kompilierte Klassen, ist in `.gitignore` |

## Achtung beim Testen

* Ohne UTF-8-Konsole sind die Umlaute kaputt und die Taste `ü` (überweisen) geht nicht. `start.cmd` regelt das.
* Ein leeres Enter im Konto-Menü lässt die App abstürzen. Das ist ein Fehler der Software (BB-18), nicht des Setups.
