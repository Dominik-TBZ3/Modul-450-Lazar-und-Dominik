# Übung 2: Funktionale Black-Box Tests einer Autovermietung

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## Getestete Plattform

**Europcar Schweiz**, <https://www.europcar.ch>, getestet am 01.09.2026 von Hand im Browser, ohne Login und ohne
Buchung. Suchparameter überall gleich: Station "Zürich Kloten Flughafen ZRH", Abholung 16.09.2026 10:00, Rückgabe
18.09.2026 10:00, Alter 26+, Wohnsitz Schweiz.

## Die 5 wichtigsten Testfälle

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------------------|---------------------|--------|------------------|
| 1 | Startseite aufrufen | Seite lädt, Suchformular ist sichtbar | Formular mit Abholort, Abhol- und Rückgabedatum, Alter und Wohnsitz ist da | OK | - |
| 2 | Fahrzeugsuche mit gültigen Daten (Kernfunktion) | Liste verfügbarer Fahrzeuge mit Tages- und Gesamtpreis | Wechsel in den Buchungsprozess, Angebote werden geladen: VW Golf CHF 60.64 / Tag, Gesamtpreis CHF 121.29, Cupra Born CHF 54.10 / CHF 108.21, Audi Q3 CHF 79.96 / CHF 159.93. Filter für Getriebe, Fahrzeugtyp, Sitze und Preis vorhanden | OK | - |
| 3 | Suche mit leerem Formular absenden | Meldung welche Felder fehlen, keine Weiterleitung | Alle drei Pflichtfelder rot markiert, u. a. "Vergessen Sie nicht eine Abholstation zu wählen.". Keine Weiterleitung | OK | - |
| 4 | Nicht existierenden Abholort eingeben (`Xyzabc`) | Meldung, dass es keinen Treffer gibt | "Es gibt leider kein Ergebnis, das mit Ihrer Suche übereinstimmt. Bitte versuchen Sie es mit einem anderen Ort, einer anderen Stadt oder einem anderen Land." | OK | - |
| 5 | Rückgabedatum vor dem Abholdatum wählen (Abholung 16.09., Rückgabe 08.09.) | Meldung, dass die Rückgabe nach der Abholung liegen muss, Abholdatum bleibt | Rückgabedatum wird auf 08.09.2026 gesetzt und das **Abholdatum stillschweigend gelöscht**. Es kommt nur "Vergessen Sie nicht ein Abholdatum zu wählen", ohne den echten Grund. Die Abholzeit 10:00 bleibt stehen | Fehler | Der Kalender lässt die ungültige Reihenfolge zu und verwirft das Abholdatum, statt die Regel zu prüfen und zu melden. Der Benutzer verliert seine Eingabe und weiss nicht warum |

## Beobachtung ohne eigenen Testfall

Bei zwei Miettagen geht Tagespreis mal Miettage nicht immer auf:

| Fahrzeug | Tagespreis | 2 × Tagespreis | Angezeigter Gesamtpreis |
|----------|------------|----------------|-------------------------|
| VW Golf | CHF 60.64 | CHF 121.28 | CHF 121.29 |
| Cupra Born | CHF 54.10 | CHF 108.20 | CHF 108.21 |
| Audi Q3 | CHF 79.96 | CHF 159.92 | CHF 159.93 |
| Audi Q2 4x4 | CHF 70.75 | CHF 141.50 | CHF 141.50 |

Der Tagespreis ist ein gerundeter Anzeigewert, der Gesamtpreis wird aus dem ungerundeten Betrag gerechnet. Für einen
Kunden sieht das nach einem Rechenfehler aus.
