# Übung 3: Bank-Software testen

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## 1. Setup, die App läuft

Wir haben das [Maven-Projekt](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/bank-software-mvn.zip)
genommen, damit man die JAR-Files nicht von Hand einbinden muss. Maven war bei uns nicht installiert, darum haben wir
die beiden ZIPs `gson.zip` und `okhttp.zip` aus dem gleichen Ordner geholt und direkt mit `javac` kompiliert.

Die ganze Software liegt bei uns im Repo unter [bank-software/](bank-software/), damit man nichts mehr herunterladen
muss. Zum Starten reicht ein Doppelklick auf `bank-software\start.cmd`. Das Skript stellt die Konsole auf UTF-8, kompiliert
beim ersten Aufruf und startet dann die App. Von Hand sind das diese zwei Befehle:

```bash
javac -encoding UTF-8 -d out -cp "libs/gson/*;libs/okhttp/*" bank-software-mvn/src/main/java/ch/tbz/bank/software/*.java
java -Dfile.encoding=UTF-8 -Dstdin.encoding=UTF-8 -cp "out;libs/gson/*;libs/okhttp/*" ch.tbz.bank.software.Main
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

## 2. Black-Box Testfälle

Alles aus der Benutzersicht, also nur über das Konsolen-Menü. Die Spalte "Effektives Resultat" ist echt, wir haben
jeden Fall durchgespielt. Startzustand ist immer der frische Programmstart.

| ID | Beschreibung | Eingabe | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------|---------------------|---------------------|--------|------------------|
| BB-01 | Programm startet und zeigt alle Konten | Start, dann `a` | Menü erscheint, 5 Konten werden aufgelistet | "Es gibt 5 Konten mit den Nummern 1-5", Liste mit Nr., Nachname und Währung ist korrekt | OK | - |
| BB-02 | Konto auswählen und Kontostand abfragen | `3`, `k` | Kontodetails und Kontostand von Musk | Nr. 3, Musk, "Aktueller Kontostand: 23500.00 CHF" | OK | - |
| BB-03 | Normal einzahlen | `4`, `e`, `250.25` | 100.50 + 250.25 = 350.75 EUR | 350.75 EUR | OK | - |
| BB-04 | Normal abheben | `3`, `a`, `500` | 23500 - 500 = 23000 CHF | 23000.00 CHF | OK | - |
| BB-05 | Genau den ganzen Kontostand abheben (Grenzwert) | `3`, `a`, `23500` | Geht durch, Saldo 0.00 | 0.00 CHF | OK | - |
| BB-06 | Mehr abheben als vorhanden | `3`, `a`, `99999999` | Fehlermeldung, Saldo unverändert | "! Kontostand zu niedrig (momentan 23500.0 CHF)." | OK | - |
| BB-07 | Konto erstellen und wieder löschen | `e`, `Meier`, `CHF`, dann `6`, `l`, `j` | Konto Nr. 6 wird angelegt und danach gelöscht | Nr. 6 Meier 0.00 CHF, dann "Konto mit Nummer 6 wurde gelöscht.", Nr. 6 nicht mehr aufrufbar | OK mit Vorbehalt | Der Startsaldo kann nicht eingegeben werden, er ist immer 0.00 |
| BB-08 | Wechselkurs abfragen | `w`, `CHF USD` | Aktueller Kurs von der API | "1 CHF = 1.230383 USD" | OK | - |
| BB-09 | Überweisung EUR auf CHF-Konto | `2`, `ü`, `3`, `1000` | 1000 EUR werden in CHF umgerechnet | "! Es wurde keine Umrechnung vorgenommen", die 1000 werden 1:1 gutgeschrieben | Fehler | `convertCurrency()` kennt nur USD-CHF, USD-EUR und CHF-USD. Alles andere landet im Fallback und wird 1:1 verbucht, statt abzubrechen |
| BB-10 | Leere Eingabe im Konto-Menü | `3`, dann nur Enter | Fehlermeldung, nochmals fragen | Absturz mit `StringIndexOutOfBoundsException: Range [0, 1) out of bounds for length 0` in `Counter.editAccount` | Fehler, kritisch | `input.substring(0,1)` wird ohne Längenprüfung aufgerufen. Bei leerer Eingabe knallt es und die ganze App ist weg. Dasselbe passiert bei der Lösch-Bestätigung |
| BB-11 | Negativen Betrag einzahlen | `3`, `e`, `-5000` | Fehlermeldung, Saldo unverändert | Saldo geht von 23500.00 auf 18500.00 CHF | Fehler, kritisch | `Account.deposit()` addiert einfach, ohne den Betrag zu prüfen. Einzahlen wird so zum Abheben, und zwar ohne Deckungsprüfung |
| BB-12 | Negativen Betrag abheben | `3`, `a`, `-5000` | Fehlermeldung, Saldo unverändert | Saldo geht von 23500.00 auf 28500.00 CHF | Fehler, kritisch | In `Account.withdraw()` ist `-5000 > balance` falsch, also wird `balance -= -5000` gerechnet. Geld aus dem Nichts |
| BB-13 | Negativen Betrag überweisen | `2`, `ü`, `3`, `-999` | Fehlermeldung | Der Sender geht von 2000.00 auf 2999.00 EUR, der Empfänger verliert 999 | Fehler, kritisch | Kombination aus BB-11 und BB-12. Damit kann man fremde Konten leerräumen |
| BB-14 | Text `NaN` als Betrag einzahlen | `3`, `e`, `NaN` | Fehlermeldung | "Aktueller Kontostand: NaN CHF", das Konto ist danach dauerhaft unbrauchbar | Fehler | `Double.parseDouble("NaN")` ist gültiges Java und liefert `Double.NaN`. Geprüft wird nur, ob sich die Eingabe parsen lässt, nicht ob eine sinnvolle Zahl herauskommt |
| BB-15 | Betragsabfrage offen lassen und die Eingabe beenden (Strg+Z bzw. EOF) | `3`, `e`, dann EOF | Programm bricht ab oder beendet sich | Endlosschleife, die "! Ungültige Eingabe, bitte nochmals!" so schnell rausschreibt, wie die Konsole mag. Wir haben in 10 Sekunden rund 1.6 Millionen Zeilen gemessen, dann mussten wir den Prozess abschiessen | Fehler, kritisch | Am Ende des Eingabestroms wirft `sc.nextLine()` eine `NoSuchElementException`. Die wird vom `catch (Exception e)` mitgefangen, die `do/while(true)`-Schleife läuft aber weiter und liest dieselbe leere Quelle nochmals |

Von den 15 Testfällen sind 7 fehlerhaft, 5 davon würden wir als kritisch einstufen, weil man damit Geld erzeugen, Geld
von einem fremden Konto wegnehmen oder die App abschiessen bzw. aufhängen kann.

Nebenbei aufgefallen, ohne eigenen Testfall: eine unbekannte Währung wie `ABC` wird beim Konto erstellen still zu USD,
der Nachname darf leer bleiben, `0.005` einzahlen zeigt 23500.01 an obwohl intern 23500.005 liegen, und Unsinn wie
`hallo` im Hauptmenü bringt gar keine Fehlermeldung.

## 3. White-Box Testfälle

Hier geht es darum, welche Methoden man sinnvoll direkt mit Unit-Tests anfassen kann. Alle Klassen liegen unter
`bank-software/bank-software-mvn/src/main/java/ch/tbz/bank/software/`.

| Methode | Warum interessant | Was wir testen würden |
|---------|-------------------|-----------------------|
| `Account.withdraw(double)` | Die einzige Methode mit `if/else` und Rückgabewert, also der klassische Kandidat für Branch Coverage | `amount < balance` gibt `true` und bucht ab, `amount == balance` als Grenzwert gibt `true` und Saldo 0, `amount > balance` gibt `false` und lässt den Saldo unverändert, dazu ein negativer `amount` (deckt BB-12 auf) |
| `Account.deposit(double)` | Nur eine Zeile, aber komplett ohne Validierung. Deckt BB-11 und BB-14 ab | Positiver Betrag addiert korrekt, 0 ändert nichts, negativer Betrag müsste abgelehnt werden, dazu `NaN` und `Double.MAX_VALUE` |
| `Counter.convertCurrency(double, Currency, Currency)` | Drei `if`-Bedingungen plus Fallback und 9 mögliche Währungskombinationen. Ideal für eine Entscheidungstabelle und Path Coverage | Alle 9 Kombinationen durchgehen. Die drei bekannten Wege müssen den Kurs anwenden, gleiche Währung darf den Betrag nicht verändern, und die restlichen Wege dürfen nicht einfach 1:1 durchlaufen (deckt BB-09 auf). Achtung: die Methode ist `private`, für den Test müsste sie mindestens package-private werden |
| `Bank.getAccount(int)` | Schleife mit Abbruchbedingung und `null` als Rückgabewert | Konto existiert, Konto existiert nicht und muss `null` liefern, leere Bank ohne Konten, dazu Nummer 0 und eine negative Nummer |
| `ExchangeRateOkhttp.getExchangeRate(String, String)` | Zugriff auf eine externe API, dazu ein `try/catch` mit zwei Rückgabewegen | Erfolgsfall gibt den Kurs zurück, Fehlerfall gibt 0.0 zurück. Ohne Mock hängt der Test am Internet und am API-Key, das gehört gemockt (Thema aus Kapitel 6) |

Nicht sinnvoll als Unit-Test testbar sind aktuell `Counter.chooseAccount()`, `Counter.editAccount(int)` und die
privaten Ein- und Auszahlungsmethoden in `Counter`. Die sitzen alle in einer `do/while(true)`-Schleife, lesen direkt vom
`Scanner` auf `System.in` und schreiben direkt auf `System.out`. Genau deshalb steht "UI von Logik trennen" unten bei
den Verbesserungen.

## 4. Was wir am Code verbessern würden

* **Betrag-Validierung fehlt komplett.** `deposit()` und `withdraw()` sollten `amount <= 0` ablehnen und mit
  `Double.isFinite()` auch `NaN` und `Infinity` abfangen. Das allein würde vier der fünf kritischen Fehler erledigen.
* **`substring(0, 1)` ohne Längenprüfung** in `editAccount()` und `getConfirmation()`. Ein `isEmpty()`-Check reicht,
  damit die App bei einem versehentlichen Enter nicht abstürzt.
* **Sammel-`catch (Exception e)` überall**, danach wird mit `instanceof` geprüft, was es war. Besser gezielt
  `NumberFormatException` fangen. So wie es jetzt ist verschwinden echte Fehler unbemerkt, und die
  `NoSuchElementException` am Ende des Eingabestroms führt in die Endlosschleife aus BB-15.
* **Geld in `double`** ist die falsche Wahl, weil Anzeige und interner Wert auseinanderlaufen. Besser `BigDecimal` oder
  ganzzahlig in Rappen als `long`.
* **Die Überweisung ist nicht atomar.** `transferAmount()` bucht zuerst beim Sender ab und rechnet erst danach um. Das
  gehört in eine `transfer()`-Methode auf `Bank`, die beide Buchungen zusammen macht und bei unbekanntem Kurs abbricht
  statt 1:1 zu verbuchen.
* **UI und Logik sind vermischt.** `Counter` liest Eingaben, gibt aus und rechnet die Fachlogik. Die Logik gehört nach
  `Bank` und `Account`, die Konsole soll nur eine dünne Schicht sein. Erst dann kann man vernünftig Unit-Tests
  schreiben.
* **Das Regex im Hauptmenü** ist `\d|a|e|w|q` mit `find()`, damit gilt jeder String als gültig, der irgendwo einen
  dieser Buchstaben enthält. Richtig wäre `matches()` mit Ankern.
* **Es gibt keine Tests.** Kein `src/test`, keine Testklasse. JUnit 5 ins `pom.xml` und los.
* **Der API-Key steht im Klartext** in `ExchangeRateOkhttp` und liegt damit im Git. Gehört in eine Konfigurationsdatei
  oder eine Umgebungsvariable.

## 5. Was uns am meisten aufgefallen ist

Die auffälligste Lücke ist nicht das Menü, sondern dass beim Betrag nie geprüft wird, ob die Zahl überhaupt Sinn macht.
Mit einem Minuszeichen kann man Abheben und Einzahlen vertauschen und sich beliebig Geld gutschreiben, und über eine
Überweisung mit negativem Betrag geht das sogar auf einem fremden Konto. Geprüft wird immer nur, ob sich die Eingabe in
eine Zahl umwandeln lässt, aber nie, ob die Zahl fachlich erlaubt ist.

Ausserdem hat sich gezeigt, wie stark die Testbarkeit von der Struktur abhängt. `Account` und `Bank` kann man direkt mit
Unit-Tests anfassen, `Counter` praktisch nicht, weil Eingabe, Ausgabe und Logik dort in einer Endlosschleife
zusammenhängen. Die meisten Fehler haben wir dann auch nur über Black-Box-Tests gefunden.
