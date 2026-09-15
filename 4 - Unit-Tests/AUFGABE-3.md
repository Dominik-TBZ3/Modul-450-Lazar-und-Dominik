# 4 - Unit-Tests

<img src="./bank/Design/bank6_klassendiagramm.png" width="90%" alt="Klassendiagramm der Banken-Simulation">

## Wie die Software funktioniert

- Beträge sind `long` in Millirappen, 1 CHF = 100'000. Kein `double`, vermutlich wegen Rundungsfehlern bei Geld
- Datum ist ein `int`, Banktage seit 1.1.1970. Jeder Monat hat 30 Tage, jedes Jahr 360
- **Bank** hält die Konten in einer `TreeMap` und vergibt Nummern ab 1000 mit Präfix: `S-` Sparkonto, `Y-` Jugendkonto, `P-` Lohnkonto
- Die Bank rechnet nichts selber, sie delegiert: Konto in der Map suchen, nicht gefunden gibt `false` bzw. keine Ausgabe
- **Account** führt Saldo und eine Liste von **Booking**. Eine Buchung hat Datum und Betrag, eine Abhebung wird negativ eingetragen
- `canTransact()` verbietet Buchungen vor der letzten Buchung, gleiches Datum ist erlaubt
- Die Kontotypen unterscheiden sich **nur** in `withdraw()` bzw. `deposit()`, alles andere ist geerbt:
  - `Account`: keine Limite, Saldo darf beliebig ins Minus
  - `SavingsAccount`: nicht ins Minus
  - `SalaryAccount`: bis zur Kreditlimite ins Minus
  - `PromoYouthSavingsAccount`: 1 % Bonus auf jede Einzahlung
- **BankUtils** formatiert Datum und Betrag, `printTop5()` / `printBottom5()` sortieren mit den zwei Comparatoren
- `Bank.getBalance()` rechnet mit `-=`, der Gesamtsaldo ist also negativ: Kundengeld ist für die Bank eine Schuld
- Alle Ausgaben gehen direkt auf `System.out`, darum leiten die Tests in Aufgabe 4 den Stream um

## Zusammenhänge

```
Bank 1 ----> * Account ----> * Booking
                  ^                |
                  |                +--> BankUtils (Formatierung)
            +--------+--------+
            |                 |
      SalaryAccount    SavingsAccount
                              ^
                              |
                  PromoYouthSavingsAccount
```

- Einbahnbeziehungen: die Bank kennt die Konten, das Konto kennt die Bank nicht. Gleich beim Konto und der Buchung. Darum ist jede Klasse für sich testbar
- Vererbung geht drei Stufen tief: `Account` -> `SavingsAccount` -> `PromoYouthSavingsAccount`
- `Bank` benutzt die beiden Comparatoren nur zum Sortieren der Auswertungen
