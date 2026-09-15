# 4 - Unit-Tests

Jeder Test startet mit einem @Test zu Beginn, somit wird die Methode als Test markiert.

## Assertions

Alle statisch aus `org.junit.jupiter.api.Assertions`, Reihenfolge immer `(erwartet, ist)`. Ein Test ist grün, solange keine Exception fliegt.

| Assertion | Beschreibung |
| --- | --- |
| `assertEquals(erwartet, ist)` | Werte vergleichen |
| `assertTrue` / `assertFalse` | boolean Rückgabewert prüfen |
| `assertNull` / `assertNotNull` | Null check |
| `assertSame` | Selbe Referenz |
| `assertNotSame` | Nicht die selbe Referenz |
| `assertArrayEquals` | Arrays vergleichen |
| `assertThrows` | erwartete Exception |

**Equals:** bei primitiven Datentypen wie int, double, float, string

**True:** bei boolean Werte, zur Prüfung ob richtig oder falsch

**Same** Haben 2 Variabeln die exakt gleiche Referenz (bei Klassen interessant)

**Throws:** Wenn Methoden Fehler schmeissen, diese korrekt erwarten, z.B. Teilen durch 0

## Referenz

Reference: [JUnit 5 User Guide](https://docs.junit.org/current/user-guide/)
