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

## 2. Black-Box Testfälle

Alles aus der Benutzersicht, also nur über das Konsolen-Menü. Die Spalte "Effektives Resultat" ist echt, wir haben
jeden Fall durchgespielt. Startzustand ist immer der frische Programmstart.

| ID | Beschreibung | Eingabe | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------|---------------------|---------------------|--------|------------------|
| BB-01 | Programm startet und zeigt das Hauptmenü | Programmstart | Menü erscheint, 5 Konten vorhanden | Wie erwartet, "Es gibt 5 Konten mit den Nummern 1-5" | OK | - |
| BB-02 | Alle Konten anzeigen | `a` | Liste mit Nr., Nachname, Währung | Alle 5 Konten korrekt aufgelistet | OK | - |
| BB-03 | Bestehendes Konto auswählen | `3` | Kontodetails von Musk | Nr. 3, Musk, 23500.00 CHF | OK | - |
| BB-04 | Kontostand abfragen | `3`, `k` | Aktueller Kontostand | "Aktueller Kontostand: 23500.00 CHF" | OK | - |
| BB-05 | Normal einzahlen | `4`, `e`, `250.25` | 100.50 + 250.25 = 350.75 EUR | 350.75 EUR | OK | - |
| BB-06 | Normal abheben | `3`, `a`, `500` | 23500 - 500 = 23000 CHF | 23000.00 CHF | OK | - |
| BB-07 | Genau den ganzen Kontostand abheben (Grenzwert) | `3`, `a`, `23500` | Geht durch, Saldo 0.00 | 0.00 CHF | OK | - |
| BB-08 | Mehr abheben als vorhanden | `3`, `a`, `99999999` | Fehlermeldung, Saldo unverändert | "! Kontostand zu niedrig (momentan 23500.0 CHF)." | OK | - |
| BB-09 | Überweisung USD auf CHF-Konto | `1`, `ü`, `3`, `100` | 100 USD abgebucht, Empfänger bekommt den umgerechneten Betrag | Sender 1400.00 USD, Empfänger 23611.00 CHF, also Kurs 1.11 | OK mit Vorbehalt | Der Kurs ist im Code fest verdrahtet. Die API im gleichen Programm sagt 1 CHF = 1.23 USD, also rund 0.81 CHF pro USD. Der verwendete Kurs ist völlig veraltet |
| BB-10 | Überweisung EUR auf CHF-Konto | `2`, `ü`, `3`, `1000` | 1000 EUR werden in CHF umgerechnet | "! Es wurde keine Umrechnung vorgenommen", die 1000 werden 1:1 gutgeschrieben | Fehler | `convertCurrency()` kennt nur USD-CHF, USD-EUR und CHF-USD. Alles andere landet im Fallback und wird 1:1 verbucht, statt abzubrechen |
| BB-11 | Überweisung auf das eigene Konto | `1`, `ü`, `1` | Fehlermeldung | "! Bitte ein anderes Konto als das momentane Konto auswählen!" | OK | - |
| BB-12 | Neues Konto erstellen | `e`, `Meier`, `CHF` | Neues Konto mit Nr. 6 | Nr. 6, Meier, 0.00 CHF | OK mit Vorbehalt | Der Startsaldo kann nicht eingegeben werden, er ist immer 0.00 |
| BB-13 | Konto mit unbekannter Währung erstellen | `e`, `Meier`, `ABC` | Nachfrage oder Abbruch | "! Die eingegebene Währung ist nicht bekannt, es wird USD verwendet.", Konto wird trotzdem angelegt | Fehler | Im `default`-Zweig wird still auf USD ausgewichen, statt nochmals zu fragen. Der Kunde bekommt ein Konto in einer Währung, die er nicht wollte |
| BB-14 | Konto mit leerem Nachnamen erstellen | `e`, Enter, `CHF` | Fehlermeldung, nochmals fragen | Konto wird angelegt, in der Liste steht dann "Nr. 6:  (CHF)" | Fehler | Der Nachname wird überhaupt nicht validiert, geprüft wird nur die Währung |
| BB-15 | Konto löschen und bestätigen | `4`, `l`, `j` | Konto ist weg | "Konto mit Nummer 4 wurde gelöscht.", Nr. 4 danach nicht mehr aufrufbar | OK | - |
| BB-16 | Konto löschen und abbrechen | `4`, `l`, `n` | Konto bleibt bestehen | "! Aktion abgebrochen.", Konto 4 ist noch mit 100.50 EUR da | OK | - |
| BB-17 | Nicht existierende Kontonummer | `99` | Fehlermeldung | "Ein Konto mit dieser Nummer ist nicht vorhanden!" | OK | - |
| BB-18 | Leere Eingabe im Konto-Menü | `3`, dann nur Enter | Fehlermeldung, nochmals fragen | Absturz mit `StringIndexOutOfBoundsException: Range [0, 1) out of bounds for length 0` in `Counter.editAccount` | Fehler, kritisch | `input.substring(0,1)` wird ohne Längenprüfung aufgerufen. Bei leerer Eingabe knallt es und die ganze App ist weg |
| BB-19 | Leere Eingabe bei der Lösch-Bestätigung | `4`, `l`, dann nur Enter | Als "nein" behandeln | Gleicher Absturz, diesmal in `Counter.getConfirmation` | Fehler, kritisch | Dasselbe `substring(0,1)`-Problem an einer zweiten Stelle |
| BB-20 | Unsinn im Hauptmenü eingeben | `hallo` | "Ungültige Eingabe" | Gar keine Meldung, das Menü erscheint einfach wieder | Fehler | Das Regex `\d\|a\|e\|w\|q` sucht mit `find()` irgendwo im String. "hallo" enthält ein "a" und gilt damit als gültig. Danach scheitert `Integer.parseInt("hallo")` und die Exception wird still verschluckt |
| BB-21 | Zahl und Buchstabe gemischt | `1a` | Konto 1 öffnen oder Fehlermeldung | Nichts, das Menü kommt ohne jeden Hinweis wieder | Fehler | Gleiche Ursache wie BB-20 |
| BB-22 | Negativen Betrag einzahlen | `3`, `e`, `-5000` | Fehlermeldung, Saldo unverändert | Saldo geht von 23500.00 auf 18500.00 CHF | Fehler, kritisch | `Account.deposit()` addiert einfach, ohne den Betrag zu prüfen. Einzahlen wird so zum Abheben, und zwar ohne Deckungsprüfung |
| BB-23 | Negativen Betrag abheben | `3`, `a`, `-5000` | Fehlermeldung, Saldo unverändert | Saldo geht von 23500.00 auf 28500.00 CHF | Fehler, kritisch | In `Account.withdraw()` ist `-5000 > balance` falsch, also wird `balance -= -5000` gerechnet. Geld aus dem Nichts |
| BB-24 | Negativen Betrag überweisen | `2`, `ü`, `3`, `-999` | Fehlermeldung | Der Sender geht von 2000.00 auf 2999.00 EUR, der Empfänger verliert 999 | Fehler, kritisch | Kombination aus BB-22 und BB-23. Damit kann man fremde Konten leerräumen |
| BB-25 | Text `NaN` als Betrag einzahlen | `3`, `e`, `NaN` | Fehlermeldung | "Aktueller Kontostand: NaN CHF", das Konto ist danach dauerhaft unbrauchbar | Fehler | `Double.parseDouble("NaN")` ist gültiges Java und liefert `Double.NaN`. Geprüft wird nur, ob sich die Eingabe parsen lässt, nicht ob eine sinnvolle Zahl herauskommt |
| BB-26 | Extrem grosse Zahl einzahlen | `3`, `e`, `1e308`, dann nochmals `1e308` | Fehlermeldung oder Obergrenze | Zuerst eine 309-stellige Zahl, nach der zweiten Einzahlung "Kontostand: Infinity CHF" | Fehler | Es gibt keinen Maximalbetrag und `double` läuft in `Infinity` über |
| BB-27 | 0 einzahlen | `3`, `e`, `0` | Hinweis, dass 0 keinen Sinn macht | Wird ohne Meldung akzeptiert, Saldo bleibt gleich | Kleiner Fehler | Keine Prüfung auf `amount <= 0`. Fachlich stört es nicht, ist aber eine sinnlose Buchung |
| BB-28 | Rappen-Rundung | `3`, `e`, `0.005` | Entweder ablehnen oder korrekt runden | Die Anzeige springt auf 23500.01 CHF, intern liegen aber 23500.005 | Fehler | `printf("%.2f")` rundet nur die Anzeige. Intern wird mit `double` weitergerechnet, Anzeige und echter Saldo laufen auseinander |
| BB-29 | Wechselkurs abfragen | `w`, `CHF USD` | Aktueller Kurs | "1 CHF = 1.230383 USD" | OK | - |
| BB-30 | Wechselkurs mit unbekannter Währung | `w`, `XXX YYY` | Fehlermeldung, nochmals fragen | "! Ungültige Eingabe oder unbekannte Währung !", danach kommt die Abfrage wieder | OK | - |
| BB-31 | Programm beenden | `q` | Programm endet | "Auf Wiedersehen!", das Programm ist beendet | OK | - |
| BB-32 | Betragsabfrage offen lassen und die Eingabe beenden (Strg+Z bzw. EOF) | `3`, `e`, dann EOF | Programm bricht ab oder beendet sich | Endlosschleife, die "! Ungültige Eingabe, bitte nochmals!" so schnell rausschreibt, wie die Konsole mag. Wir haben in 10 Sekunden rund 1.6 Millionen Zeilen gemessen, dann mussten wir den Prozess abschiessen | Fehler, kritisch | Am Ende des Eingabestroms wirft `sc.nextLine()` eine `NoSuchElementException`. Die wird vom `catch (Exception e)` mitgefangen, die `do/while(true)`-Schleife läuft aber weiter und liest dieselbe leere Quelle nochmals. Dasselbe passiert, wenn man nach `q` (dort wird der Scanner geschlossen) noch weitertippen könnte |

Zusammengezählt sind das 32 Testfälle, davon 15 mit einem Fehler. Sechs davon würden wir als kritisch einstufen, weil man
damit Geld erzeugen, Geld von einem fremden Konto wegnehmen oder die App komplett abschiessen bzw. aufhängen kann.

## 3. White-Box Testfälle

Hier geht es darum, welche Methoden man sinnvoll direkt mit Unit-Tests anfassen kann. Alle Klassen liegen unter
`src/main/java/ch/tbz/bank/software/`.

| Methode | Warum interessant | Was wir testen würden |
|---------|-------------------|-----------------------|
| `Account.withdraw(double)` | Die einzige Methode mit `if/else` und Rückgabewert, also der klassische Kandidat für Branch Coverage | `amount < balance` gibt `true` und bucht ab, `amount == balance` als Grenzwert gibt `true` und Saldo 0, `amount > balance` gibt `false` und lässt den Saldo unverändert, negativer `amount` (deckt BB-23 auf), `amount = NaN` |
| `Account.deposit(double)` | Nur eine Zeile, aber komplett ohne Validierung. Deckt BB-22, BB-25 und BB-26 ab | Positiver Betrag addiert korrekt, 0 ändert nichts, negativer Betrag müsste abgelehnt werden, dazu `NaN` und `Double.MAX_VALUE` |
| `Account.getBalance()` | Der Prüfpunkt für alle anderen Tests | Direkt nach dem Konstruktor gleich dem Startsaldo, nach Buchungen der korrekte Wert |
| `Counter.convertCurrency(double, Currency, Currency)` | Drei `if`-Bedingungen plus Fallback und 9 mögliche Währungskombinationen. Ideal für eine Entscheidungstabelle und Path Coverage | Alle 9 Kombinationen durchgehen. Die drei bekannten Wege müssen den Kurs anwenden, gleiche Währung darf den Betrag nicht verändern, und die restlichen Wege dürfen nicht einfach 1:1 durchlaufen (deckt BB-10 auf). Achtung: die Methode ist `private`, für den Test müsste sie mindestens package-private werden |
| `Bank.getAccount(int)` | Schleife mit Abbruchbedingung und `null` als Rückgabewert | Konto existiert, Konto existiert nicht und muss `null` liefern, leere Bank ohne Konten, Nummer 0 und negative Nummer |
| `Bank.createAccount(...)` und `Bank.getNumberOfAccounts()` | Zustand der internen Liste prüfen | Nach n Aufrufen sind n Konten drin, das zurückgegebene Konto ist auch in der Liste, Startsaldo und Währung stimmen |
| `Bank.deleteAccount(Account)` | Verändert die Liste | Nach dem Löschen ist die Anzahl um 1 kleiner, `getAccount()` liefert für die Nummer `null`, dasselbe Konto zweimal löschen darf nicht knallen, dazu `null` als Parameter |
| `Account`-Konstruktor mit `static int counter` | Der Zähler ist statisch und läuft über alle Instanzen hinweg weiter | Das erste Konto hat ID 1. Genau das ist aber ein Testproblem: sobald ein anderer Test vorher Konten anlegt, stimmen die IDs nicht mehr. Der Test hängt dann von der Ausführungsreihenfolge ab, was man eigentlich nie will |
| `ExchangeRateOkhttp.getExchangeRate(String, String)` | Zugriff auf eine externe API, dazu ein `try/catch` mit zwei Rückgabewegen | Erfolgsfall gibt den Kurs zurück, Fehlerfall gibt 0.0 zurück. Ohne Mock hängt der Test am Internet und am API-Key, das gehört gemockt (Thema aus Kapitel 6) |
| `Counter.getConfirmation()` | Nur zwei Ausgänge, `j` oder alles andere. Deckt BB-19 ab | `j` gibt `true`, `n` gibt `false`, grosses `J` gibt `true`, leere Eingabe darf nicht abstürzen. Braucht aber einen einspeisbaren `Scanner` |

Nicht sinnvoll als Unit-Test testbar sind aktuell `Counter.chooseAccount()`, `Counter.editAccount(int)`,
`Counter.deposit()`, `Counter.withdraw()` und `Counter.transfer()`. Die sitzen alle in einer `do/while(true)`-Schleife,
lesen direkt vom `Scanner` auf `System.in` und schreiben direkt auf `System.out`. Man kann sie höchstens mit
umgebogenen Streams testen, und das wird schnell unübersichtlich. Genau deshalb steht "UI von Logik trennen" unten bei
den Verbesserungen.

