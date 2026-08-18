# Übung 3: Bank-Software testen

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## 1. Setup, die App läuft

Wir haben das [Maven-Projekt](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/bank-software-mvn.zip)
genommen, damit man die JAR-Files nicht von Hand einbinden muss. Maven war bei uns nicht installiert, darum haben wir
die beiden ZIPs `gson.zip` und `okhttp.zip` aus dem gleichen Ordner geholt und direkt mit `javac` kompiliert:

```bash
javac -encoding UTF-8 -d out -cp "libs/gson/*.jar;libs/okhttp/*.jar" src/main/java/ch/tbz/bank/software/*.java
```

Starten dann mit:

```bash
java -cp "out;libs/gson/*.jar;libs/okhttp/*.jar" ch.tbz.bank.software.Main
```

Läuft mit JDK 25 durch, obwohl im `pom.xml` Java 20 steht. Beim Start gibt es fünf vorbereitete Konten:

```
[Es gibt 5 Konten mit den Nummern 1-5.]

Willkommen am Schalter 1!
___
Was möchten Sie tun? Tippen Sie ...
eine Kontonummer, um das Konto zu bearbeiten.
"a" für alle Konten anzeigen.
"e" für Konto erstellen.
"w" für Wechselkurs abfragen.
"q" für Beenden.
> Eingabe: a
Nr. 1: Rockefeller (USD)
Nr. 2: Gates (EUR)
Nr. 3: Musk (CHF)
Nr. 4: Bezos (EUR)
Nr. 5: Branson (USD)
```

Zwei Sachen, die uns beim Aufsetzen sofort aufgefallen sind:

* Auf der Windows-Konsole sind alle Umlaute kaputt, solange man nicht auf UTF-8 (`chcp 65001`) umstellt. Aus
  "Nummern 1-5" wird dann `Nummern 1?5`.
* Im Konto-Menü ist "überweisen" auf die Taste `ü` gelegt. Auf einer Konsole ohne UTF-8 kommt man da fast nicht rein.
  Für ein Testsetup und für Leute mit einem anderen Tastaturlayout ist das eine schlechte Wahl.

Die Wechselkurs-Abfrage über die API funktioniert übrigens noch, der API-Key im Code ist nicht abgelaufen.

