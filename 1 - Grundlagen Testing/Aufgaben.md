# 1 - Grundlagen Testing: Aufgaben

## Aufgabe 1: Formen von Tests

**Unit-Test**
- Einzelne Methode oder Klasse für sich allein
- Vom Entwickler geschrieben, automatisch mit NUnit bei jedem Build
- Beispiel: Preisberechnung mit verschiedenen Rabatten aufrufen

**Integrationstest**
- Prüft, ob mehrere Teile zusammenspielen (DB, API, fremder Service)
- Automatisiert, aber mit Testdatenbank oder Container, läuft in der Pipeline
- Beispiel: Bestellung speichern und prüfen, ob sie in der DB steht

**Lasttest**
- Nicht ob richtig gerechnet wird, sondern ob es unter Last schnell genug ist
- Auf einer Testumgebung, Vorgabe vorher festlegen
- Beispiel: Webshop mit 1000 gleichzeitigen Bestellungen

## Aufgabe 2: Fehler und Mangel

**SW-Fehler:** MWST wird mit 7.7 % gerechnet, gefordert sind 8.1 %. SOLL und IST weichen ab.

**SW-Mangel:** Betrag ist richtig, im PDF steht aber 1199.5 statt 1 199.50. Funktioniert, aber nicht gut.

**Hoher Schaden:** Ariane 5, 1996. Zahl in zu kleinen Datentyp umgewandelt, Rakete nach 40 Sekunden zerstört, Schaden mehrere hundert Millionen.

## Aufgabe 3: Testtreiber

[Preisberechnung.java](Preisberechnung.java), [TestTreiber.java](TestTreiber.java).

Der Treiber ruft die Methode mit festen Werten auf, vergleicht mit dem von Hand berechneten SOLL und gibt am Schluss zurück, ob alle Testfälle OK sind.

Grundpreis 20000, Sondermodell 1000, Zubehör 1000 bleiben gleich:

| Nr | Extras | Rabatt | Erwartet | Resultat |
| --- | --- | --- | --- | --- |
| 1 | 0 | 0 % | 22000.00 | OK |
| 2 | 2 | 0 % | 22000.00 | OK |
| 3 | 3 | 0 % | 21900.00 | OK |
| 4 | 5 | 0 % | 21850.00 | Fehler, ist 21900.00 |
| 5 | 0 | 20 % | 18000.00 | Fehler, ist 17800.00 |
| 6 | 3 | 20 % | 17900.00 | Fehler, ist 17800.00 |

## Aufgabe 3 Bonus: Was falsch ist

**Fehler:** Bedingungen in falscher Reihenfolge. Bei 5 Extras trifft "3 oder mehr" schon zu, die 15 % werden nie erreicht. Korrektur: zuerst auf 5 prüfen, dann auf 3.