# Aufgabe 2

Starten und Testen wie in [UEBUNG-1.md](UEBUNG-1.md).

## Was wir gemacht haben

* **Comparator erweitert**: Er kann jetzt nach jedem Attribut einer Adresse sortieren und in beide
  Richtungen. Dafür sind zwei Enums dazugekommen:

  ```java
  public enum SortField { ID, FIRSTNAME, LASTNAME, PHONENUMBER, REGISTRATION_DATE }
  public enum SortDirection { ASC, DESC }
  ```

  Beispiele: `new AddressComparator(FIRSTNAME)`, `new AddressComparator(DESC, PHONENUMBER)` oder
  `new AddressComparator(REGISTRATION_DATE, LASTNAME)` für zwei Felder nacheinander.
  Der leere Konstruktor bleibt und sortiert wie in Aufgabe 1, damit der Service unverändert läuft.

* **Neue Funktionalität getestet** — 19 zusätzliche Tests. Die Testadressen sind so gewählt, dass jedes
  Attribut eine andere Reihenfolge ergibt, ein Test kann also nicht zufällig grün sein. Getestet werden
  alle fünf Felder, beide Richtungen, die Kombination von zwei Feldern und die Fehlerfälle
  (leere Feldliste, `null` als Feld oder Richtung → `IllegalArgumentException`).

  Zwei Tests laufen über `@EnumSource`, damit ein neues `SortField` automatisch mitgetestet wird.

## Nicht gemacht

Die Sortierung über die REST-Schnittstelle wählbar zu machen war nicht Teil der Aufgabe.
Der Comparator wäre dafür bereit, es fehlt nur das Durchreichen vom Controller zum Service.
