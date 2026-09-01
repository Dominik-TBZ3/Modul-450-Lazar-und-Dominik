# Übung 1: Rabattregeln Autohaus

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## Ausgangslage

> Bei einem Kaufpreis von weniger als 15'000 CHF soll kein Rabatt gewährt werden. Bei einem Preis bis zu 20'000 CHF sind
> 5% Rabatt angemessen. Liegt der Kaufpreis unter 25'000 CHF sind 7% Rabatt möglich, darüber sind 8,5 % Rabatt zu
> gewähren.

Daraus ergeben sich vier gültige Äquivalenzklassen. Achtung auf die Formulierung: "bis zu 20'000" schliesst die 20'000
ein, "unter 25'000" schliesst die 25'000 aus.

## 1. Abstrakte Testfälle

`p` ist der Kaufpreis in CHF.

| ID | Äquivalenzklasse | Bedingung | Erwarteter Rabatt | Erwarteter Endpreis |
|----|------------------|-----------|-------------------|---------------------|
| A-01 | Kein Rabatt | `p < 15000` | 0 % | `p` |
| A-02 | Kleiner Rabatt | `15000 <= p <= 20000` | 5 % | `p * 0.95` |
| A-03 | Mittlerer Rabatt | `20000 < p < 25000` | 7 % | `p * 0.93` |
| A-04 | Grosser Rabatt | `p > 25000` | 8,5 % | `p * 0.915` |
| A-05 | Spezifikationslücke | `p == 25000` | nicht spezifiziert | - |
| A-06 | Ungültige Eingabe | `p <= 0` | Fehlermeldung erwartet | - |
| A-07 | Ungültige Eingabe | `p` nicht numerisch | Fehlermeldung erwartet | - |

A-05 bis A-07 stehen nicht in der Beschreibung, treten in einer echten Software aber auf.

## 2. Konkrete Testfälle

Pro Klasse ein Repräsentant plus die Grenzwerte links und rechts jeder Klassengrenze.

| ID | Kaufpreis CHF | Klasse | Art | Erwarteter Rabatt | Erwarteter Endpreis CHF |
|----|---------------|--------|-----|-------------------|-------------------------|
| K-01 | 10'000.00 | A-01 | Repräsentant | 0 % | 10'000.00 |
| K-02 | 14'999.99 | A-01 | Grenzwert | 0 % | 14'999.99 |
| K-03 | 15'000.00 | A-02 | Grenzwert | 5 % | 14'250.00 |
| K-04 | 17'500.00 | A-02 | Repräsentant | 5 % | 16'625.00 |
| K-05 | 20'000.00 | A-02 | Grenzwert, "bis zu" schliesst ein | 5 % | 19'000.00 |
| K-06 | 20'000.01 | A-03 | Grenzwert | 7 % | 18'600.01 |
| K-07 | 22'500.00 | A-03 | Repräsentant | 7 % | 20'925.00 |
| K-08 | 24'999.99 | A-03 | Grenzwert | 7 % | 23'249.99 |
| K-09 | 25'000.00 | A-05 | Spezifikationslücke | unklar: 7 % oder 8,5 % | 23'250.00 oder 22'875.00 |
| K-10 | 25'000.01 | A-04 | Grenzwert | 8,5 % | 22'875.01 |
| K-11 | 50'000.00 | A-04 | Repräsentant | 8,5 % | 45'750.00 |
| K-12 | 0.00 | A-06 | Negativtest | Fehlermeldung | keine Berechnung |
| K-13 | -5'000.00 | A-06 | Negativtest | Fehlermeldung | keine Berechnung |
| K-14 | `zwanzigtausend` | A-07 | Negativtest | Fehlermeldung | keine Berechnung |

Die Grenzwerte K-02, K-03, K-05, K-06, K-08 und K-10 sind die wichtigsten Fälle. Ein verwechseltes `<` und `<=` fällt
nur dort auf, ein Test mit 17'500 und 22'500 allein findet so einen Fehler nie.
