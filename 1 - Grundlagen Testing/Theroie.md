# Modul 450

## 1. Grundlagen Testing

### Wieso Testen?

- Software steckt heute fast überall drin: Geräte, Maschinen, Anlagen
- Aber auch im Betrieb, z.B. Bestellwesen oder Rechnungswesen
- Ganze Firmen hängen davon ab, dass die Software zuverlässig läuft
- Qualität ist deshalb sehr wichtig für den Erfolg
- Testen ist das Mittel, um diese Qualität zu erreichen

### Ursprung in der Produktion

- Die Idee kommt aus der Industrieproduktion
- Dort prüft man, ob das Produkt die geforderte Aufgabe löst
- Ist es fehlerhaft, korrigiert man die Herstellung oder die Konstruktion
- Bei Software ist es gleich, sicherstellen, ob es funktioniert

### Fehler vs Mangel

**Fehler**
- Eine Anforderung wird nicht erfüllt
- Abweichung zwischen IST (was das System im Test macht) und SOLL (was im Ticket steht)

**Mangel**
- Eine Anforderung oder eine berechtigte Erwartung wird nicht angemessen erfüllt
- Es funktioniert grundsätzlich, aber nicht gut

### Fehlermaskierung

- Ein Fehler wird von einem anderen Programmteil kompensiert und ist darum nicht sichtbar
- Er kommt erst raus, wenn der andere Teil korrigiert wird
- Fehler können also voneinander abhängen
- Kann auch zeitverzögert auftreten, z.B. Daten werden falsch gespeichert und erst viel später verwendet

### Kann alles getestet werden?

- Nein, nur weil es getestet ist, kann es immernoch zu Fehlern kommen
- Zu viele Kombinationen aus Eingaben, Zuständen und Randbedingungen
- Die Anzahl möglicher Testfälle ist praktisch unbegrenzt
- Testen zeigt, dass Fehler da sind. Es zeigt nicht, dass keine da sind

### Kriterien für gute Testfälle

- Hohe Wahrscheinlichkeit, einen Fehler zu finden
- Keine Redundanz, also nicht zweimal das Gleiche testen
- Unabhängig voneinander
- Möglichst viel Code abdecken (Codeabdeckung / Coverage)
- Für die Coverage gibt es Werkzeuge

### Testaufwand

- Ein vollständiger Test ist nicht möglich, darum muss der Aufwand zum Nutzen passen
- Systeme mit grossem Schaden bei einem Fehler werden intensiver getestet
- Systeme mit kleinem Schaden werden weniger intensiv getestet
- Schaden ist nicht nur finanziell, es kann auch um Leben und Gesundheit gehen

### Testing in einem Vorgehensmodell

- In der Software-Entwicklung gibt es verschiedene Vorgehensmodelle
- Festlegung, nach welcher Struktur ein Projekt umgesetzt wird
- Zwei Modelle haben die Art des Testens stark verändert: das V-Modell und SCRUM
- Im Wasserfallmodell kommt das Testen erst ganz am Schluss

### Das V-Modell als Prototyp für verschiedene Testarten

- Erweiterung vom klassischen Wasserfallmodell
- Testen wird dem Entwickeln gleichgesetzt, nicht hinten angehängt
- Wird als V dargestellt: links Entwicklung, rechts Integration und Test

Linker Ast (Entwicklung, wird immer detaillierter)
- Anforderungsdefinition: Wünsche und Anforderungen vom Auftraggeber
- Funktionaler Systementwurf: Anforderungen auf Funktionen und Dialoge abbilden
- Technischer Systementwurf: technische Realisierung, Komponenten und Schnittstellen
- Komponentenspezifikation: jedes Teilsystem im Detail beschreiben
- Programmierung: jeder Baustein wird programmiert

Rechter Ast (Integration und Test, wird wieder zusammengesetzt)
- Komponententest: erfüllt der einzelne Baustein seine Vorgaben
- Integrationstest: spielen die Komponenten korrekt zusammen
- Systemtest: erfüllt das System als Ganzes die Anforderungen
- Abnahmetest: akzeptiert der Kunde das System

- Jede Testart hat eigene Methoden und Werkzeuge
- Auch das Personal ist unterschiedlich: der Programmierer macht den Komponententest, der Kunde ist beim Abnahmetest dabei
- Es sieht aus, als würde man erst spät testen. Das stimmt nicht, die Teststufen laufen parallel zur Entwicklung

### SCRUM als Beispiel für das iterative Testen

- Iterativ, in Sprints
- Das Produkt wird nicht an einem Stück entwickelt, sondern in einer Abfolge von Versionen
- Jede Iteration gibt eine verbesserte Version
- Nach der Definition of Done entsteht ein Increment, nach dem Review gilt es als auslieferbar
- Die gleichen Teststufen gibt es auch, aber in jedem Sprint statt einmal am Schluss
- Tests müssen wiederverwendbar sein, weil sie bei jedem Increment wieder laufen
- Darum sind automatisierte Tests hier so wichtig
- Mit mehr Funktionalität wachsen auch die Testfälle, manuell wird das zu viel
