public class TestTreiber {

    public static void main(String[] args) {
        boolean ergebnis = test_calculate_price();
        System.out.println();
        System.out.println("Alle Testfaelle OK? " + ergebnis);
    }

    static boolean test_calculate_price() {

        double price;
        boolean test_ok = true;

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 0, 0);
        System.out.println("Test 1  soll 22000.0  ist " + price);
        if (price != 22000.0) {
            test_ok = false;
        }

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 2, 0);
        System.out.println("Test 2  soll 22000.0  ist " + price);
        if (price != 22000.0) {
            test_ok = false;
        }

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 3, 0);
        System.out.println("Test 3  soll 21900.0  ist " + price);
        if (price != 21900.0) {
            test_ok = false;
        }

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 5, 0);
        System.out.println("Test 4  soll 21850.0  ist " + price);
        if (price != 21850.0) {
            test_ok = false;
        }

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 0, 20);
        System.out.println("Test 5  soll 18000.0  ist " + price);
        if (price != 18000.0) {
            test_ok = false;
        }

        price = Preisberechnung.calculatePrice(20000, 1000, 1000, 3, 20);
        System.out.println("Test 6  soll 17900.0  ist " + price);
        if (price != 17900.0) {
            test_ok = false;
        }

        return test_ok;
    }
}
