# Übung 2: Funktionale Black-Box Tests einer Autovermietung

Lazar & Dominik, Modul 450

Aufgabe: [UEBUNGEN.md aus dem M450-Repo](https://gitlab.com/ch-tbz-it/Stud/m450/m450/-/blob/main/Unterlagen/teststrategie/UEBUNGEN.md)

## Getestete Plattform

**Europcar Schweiz**, <https://www.europcar.ch>

Getestet am 01.09.2026 im Browser bei einer Fensterbreite von 1440 px. Wir haben die Tests von Hand über die Webseite
ausgeführt, ohne Login und ohne Buchung. Die Spalte "Effektives Resultat" enthält, was wir tatsächlich gesehen haben.

Als Suchparameter haben wir überall dieselben Werte verwendet, damit die Fälle vergleichbar bleiben:
Station "Zürich Kloten Flughafen ZRH", Abholung 16.09.2026 10:00, Rückgabe 18.09.2026 10:00, Alter 26+, Wohnsitz Schweiz.

## Die 5 wichtigsten Testfälle

Wir haben die Testfälle danach ausgewählt, was man braucht, damit die Plattform überhaupt betreibbar ist: Die Seite muss
erreichbar sein, die Suche muss Angebote mit Preisen liefern, und Fehleingaben dürfen nicht in einer kaputten Buchung
enden.

| ID | Beschreibung | Erwartetes Resultat | Effektives Resultat | Status | Mögliche Ursache |
|----|--------------|---------------------|---------------------|--------|------------------|
| 1 | Startseite aufrufen | Die Seite lädt und das Suchformular mit Abholort, Datum und Zeit ist sichtbar | Seite lädt, Formular mit "Abhol- und Rückgabeort", Abhol- und Rückgabedatum, Alter und Wohnsitz ist da. Titel "Autovermietung - Auto, Transporter mieten \| Europcar Schweiz" | OK | - |
| 2 | Fahrzeugsuche mit gültigen Daten (Kernfunktion) | Liste verfügbarer Fahrzeuge mit Tages- und Gesamtpreis | Wechsel in den Buchungsprozess (Schritt 1 Mietort, Schritt 2 Fahrzeug). Angebote werden geladen, z. B. VW Golf CHF 60.64 pro Tag / Gesamtpreis CHF 121.29, Cupra Born CHF 54.10 / CHF 108.21, Audi Q3 CHF 79.96 / CHF 159.93. Filter für Getriebe, Fahrzeugtyp, Sitze und Preis vorhanden | OK | - |
| 3 | Suche mit leerem Formular absenden | Verständliche Meldung, welche Felder fehlen, keine Weiterleitung | Alle drei Pflichtfelder werden rot markiert mit den Meldungen "Vergessen Sie nicht eine Abholstation zu wählen.", "Vergessen Sie nicht, ein Abholdatum und eine Abholzeit zu wählen" und "Vergessen Sie nicht Rückgabezeit und -datum zu wählen". Es wird nicht weitergeleitet | OK | - |
| 4 | Nicht existierenden Abholort eingeben (`Xyzabc`) | Meldung, dass es keinen Treffer gibt, keine leere Ergebnisliste | Im Vorschlagsfeld erscheint "Es gibt leider kein Ergebnis, das mit Ihrer Suche übereinstimmt. Bitte versuchen Sie es mit einem anderen Ort, einer anderen Stadt oder einem anderen Land." Das Pflichtfeld bleibt als offen markiert | OK | - |
| 5 | Rückgabedatum vor dem Abholdatum wählen (Abholung 16.09., Rückgabe 08.09.) | Meldung, dass die Rückgabe nach der Abholung liegen muss, Abholdatum bleibt erhalten | Das Rückgabedatum wird auf 08.09.2026 gesetzt und das bereits gewählte **Abholdatum wird stillschweigend gelöscht**. Es erscheint nur "Vergessen Sie nicht ein Abholdatum zu wählen", ohne Hinweis auf den eigentlichen Grund. Die Abholzeit 10:00 bleibt dabei stehen, das Formular ist also in einem halb gefüllten Zustand | Fehler | Der Kalender lässt eine ungültige Reihenfolge zu und löst den Konflikt, indem er das Abholdatum verwirft, statt eine Regel zu prüfen und zu melden. Der Benutzer verliert seine Eingabe und weiss nicht warum |

## Weitere Beobachtung ohne eigenen Testfall

Bei den Angeboten aus Testfall 2 geht Tagespreis mal Miettage nicht immer genau auf. Die Mietdauer ist zwei Tage:

| Fahrzeug | Tagespreis | 2 × Tagespreis | Angezeigter Gesamtpreis | Differenz |
|----------|------------|----------------|-------------------------|-----------|
| VW Golf | CHF 60.64 | CHF 121.28 | CHF 121.29 | + 0.01 |
| Cupra Born | CHF 54.10 | CHF 108.20 | CHF 108.21 | + 0.01 |
| Audi Q3 | CHF 79.96 | CHF 159.92 | CHF 159.93 | + 0.01 |
| Audi Q2 4x4 | CHF 70.75 | CHF 141.50 | CHF 141.50 | 0.00 |

Der Tagespreis ist offensichtlich ein gerundeter Anzeigewert und der Gesamtpreis wird aus dem ungerundeten Betrag
gerechnet. Fachlich ist das nicht falsch, für einen Kunden sieht es aber nach einem Rechenfehler aus. Als Testfall würde
man das als "Preisplausibilität" aufnehmen und mit dem Product Owner klären, welcher der beiden Werte der verbindliche
ist.

## Was wir bewusst nicht getestet haben

Die Plattform gehört uns nicht, darum haben wir alles weggelassen, was echte Daten oder eine echte Buchung erzeugt:

* **Buchung abschliessen und bezahlen.** Das würde eine echte Reservation und eine Zahlung auslösen. In einem richtigen
  Projekt macht man das auf einer Testumgebung mit Testkreditkarten, nicht auf der Produktivseite.
* **Registrierung und Login.** Dafür bräuchte man ein Konto mit echten Personendaten.
* **Kontakt- und Newsletter-Formulare.** Die verschicken Nachrichten an echte Mitarbeitende.

Diese Fälle gehören in eine vollständige Teststrategie klar dazu, sie brauchen aber eine Testumgebung. Genau das ist
auch der Grund, warum man in einem Projekt eine eigene Stage will und nicht auf der Produktion testet.
