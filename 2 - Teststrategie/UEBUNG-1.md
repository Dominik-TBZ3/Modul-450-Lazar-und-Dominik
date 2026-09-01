# Übung 1: Rabattregeln Autohaus

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## Ausgangslage

> Über die Verkaufssoftware kann das Autohaus seinen Verkäufern Rabattregeln vorgeben: Bei einem Kaufpreis von weniger
> als 15'000 CHF soll kein Rabatt gewährt werden. Bei einem Preis bis zu 20'000 CHF sind 5% Rabatt angemessen. Liegt der
> Kaufpreis unter 25'000 CHF sind 7% Rabatt möglich, darüber sind 8,5 % Rabatt zu gewähren.

Aus dem Text lesen wir vier Äquivalenzklassen heraus. Wichtig ist der Unterschied in der Formulierung: "bis zu 20'000"
schliesst die 20'000 mit ein, "unter 25'000" schliesst die 25'000 aus. Genau daraus entsteht die Lücke, die unten unter
"Unklarheiten" steht.

| Klasse | Kaufpreis | Rabatt |
|--------|-----------|--------|
| 1 | weniger als 15'000 | 0 % |
| 2 | 15'000 bis 20'000 | 5 % |
| 3 | über 20'000 und unter 25'000 | 7 % |
| 4 | über 25'000 | 8,5 % |

## 1. Abstrakte Testfälle

Hier steht die Bedingung mit logischen Operatoren, ohne konkrete Zahlen. `p` ist der Kaufpreis in CHF.

| ID | Äquivalenzklasse | Bedingung | Erwarteter Rabatt | Erwarteter Endpreis |
|----|------------------|-----------|-------------------|---------------------|
| A-01 | Kein Rabatt | `p < 15000` | 0 % | `p` |
| A-02 | Kleiner Rabatt | `15000 <= p <= 20000` | 5 % | `p * 0.95` |
| A-03 | Mittlerer Rabatt | `20000 < p < 25000` | 7 % | `p * 0.93` |
| A-04 | Grosser Rabatt | `p > 25000` | 8,5 % | `p * 0.915` |
| A-05 | Spezifikationslücke | `p == 25000` | nicht spezifiziert, muss geklärt werden | - |
| A-06 | Ungültige Eingabe | `p <= 0` | nicht spezifiziert, wir erwarten eine Fehlermeldung | - |
| A-07 | Ungültige Eingabe | `p` ist nicht numerisch | Fehlermeldung, keine Berechnung | - |

Die Klassen A-01 bis A-04 sind die gültigen Klassen aus der Beschreibung. A-05 bis A-07 haben wir ergänzt, weil sie in
der Beschreibung fehlen, in einer echten Software aber auftreten.

## 2. Konkrete Testfälle

Pro Klasse ein Repräsentant aus der Mitte plus die Grenzwerte direkt links und rechts von jeder Klassengrenze
(Grenzwertanalyse mit zwei Werten). Wir rechnen auf Rappen genau und runden auf zwei Stellen.

| ID | Kaufpreis CHF | Klasse | Art | Erwarteter Rabatt | Erwarteter Endpreis CHF |
|----|---------------|--------|-----|-------------------|-------------------------|
| K-01 | 1.00 | A-01 | Repräsentant | 0 % | 1.00 |
| K-02 | 10'000.00 | A-01 | Repräsentant | 0 % | 10'000.00 |
| K-03 | 14'999.99 | A-01 | Grenzwert unter 15'000 | 0 % | 14'999.99 |
| K-04 | 15'000.00 | A-02 | Grenzwert, erste Stufe greift | 5 % | 14'250.00 |
| K-05 | 17'500.00 | A-02 | Repräsentant | 5 % | 16'625.00 |
| K-06 | 20'000.00 | A-02 | Grenzwert, "bis zu" schliesst ein | 5 % | 19'000.00 |
| K-07 | 20'000.01 | A-03 | Grenzwert, nächste Stufe greift | 7 % | 18'600.01 |
| K-08 | 22'500.00 | A-03 | Repräsentant | 7 % | 20'925.00 |
| K-09 | 24'999.99 | A-03 | Grenzwert unter 25'000 | 7 % | 23'249.99 |
| K-10 | 25'000.00 | A-05 | Spezifikationslücke | unklar: 7 % oder 8,5 % | 23'250.00 oder 22'875.00 |
| K-11 | 25'000.01 | A-04 | Grenzwert, letzte Stufe greift | 8,5 % | 22'875.01 |
| K-12 | 50'000.00 | A-04 | Repräsentant | 8,5 % | 45'750.00 |
| K-13 | 0.00 | A-06 | Negativtest | Fehlermeldung erwartet | keine Berechnung |
| K-14 | -5'000.00 | A-06 | Negativtest | Fehlermeldung erwartet | keine Berechnung |
| K-15 | `zwanzigtausend` | A-07 | Negativtest | Fehlermeldung erwartet | keine Berechnung |

K-04, K-06, K-07, K-09 und K-11 sind die interessantesten Fälle. Ein typischer Programmierfehler ist, `<` und `<=` zu
verwechseln, und genau das fällt nur bei diesen Werten auf. Ein Test mit 17'500 und 22'500 allein würde so einen Fehler
nie finden.

## 3. Unklarheiten in der Beschreibung

Beim Ableiten der Testfälle sind uns vier Stellen aufgefallen, die man vor der Umsetzung klären müsste:

1. **Bei genau 25'000 CHF steht nichts.** 25'000 liegt nicht "unter 25'000" und die Formulierung "darüber" trifft auch
   nicht zu. Der Fall fällt zwischen die beiden Regeln. Das ist K-10 und der Grund, warum wir dort keinen Erwartungswert
   hinschreiben können.
2. **Die Grenzen sind unterschiedlich formuliert.** "bis zu 20'000" schliesst die 20'000 ein, "unter 25'000" schliesst
   die 25'000 aus, "weniger als 15'000" ebenfalls. Wenn man das nicht bemerkt, baut man die Bedingungen falsch.
3. **Nach unten gibt es keine Grenze.** Gilt "kein Rabatt" auch bei 0 CHF oder bei einem negativen Preis? Für eine
   Software muss das entschieden werden, sonst rechnet sie irgendetwas.
4. **"angemessen" und "möglich" sind keine Vorgaben.** Nur bei 8,5 % steht "zu gewähren". Bei 5 % steht "angemessen"
   und bei 7 % "möglich". Ist das ein fester Wert oder ein Maximum, das der Verkäufer auch unterschreiten darf? Davon
   hängt ab, ob man einen einzelnen Wert oder einen Bereich testet.

Ausserdem sagt die Beschreibung nichts zur Rundung. Bei K-07 sind es exakt 18'600.0093 CHF, bei K-09 23'249.9907 CHF.
Wir runden auf Rappen, aber im Autohandel wäre auch eine Rundung auf 5 Rappen oder auf ganze Franken denkbar. Solange
das nicht definiert ist, kann der Test bei diesen Werten willkürlich fehlschlagen.
