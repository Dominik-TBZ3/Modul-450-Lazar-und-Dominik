import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorTest {

	private Calculator calculator;

	@BeforeEach
	void setUp() {
		calculator = new Calculator();
	}

	@Test
	void addZweiPositiveZahlen() {
		var result = calculator.addieren(2.0, 3.0);
		assertEquals(5.0, result);
	}

	@Test
	void addMitNegativerZahl() {
		var result = calculator.addieren(2.0, -3.0);
		assertEquals(-1.0, result);
	}

	@Test
	void addMitNull() {
		var result = calculator.addieren(3.0, 0.0);
		assertEquals(3.0, result);
	}

	@Test
	void subtractPositivesErgebnis() {
		var result = calculator.subtrahieren(5.0, 3.0);
		assertEquals(2.0, result);
	}

	@Test
	void subtractNegativesErgebnis() {
		var result = calculator.subtrahieren(3.0, 5.0);
		assertEquals(-2.0, result);
	}

	@Test
	void subtractMitNull() {
		var result = calculator.subtrahieren(3.0, 0.0);
		assertEquals(3.0, result);
	}

	@Test
	void multiplyZweiPositiveZahlen() {
		var result = calculator.multiplizieren(4.0, 3.0);
		assertEquals(12.0, result);
	}

	@Test
	void multiplyMitNegativerZahl() {
		var result = calculator.multiplizieren(4.0, -3.0);
		assertEquals(-12.0, result);
	}

	@Test
	void multiplyMitNull() {
		var result = calculator.multiplizieren(4.0, 0.0);
		assertEquals(0.0, result);
	}

	@Test
	void divideOhneRest() {
		var result = calculator.dividieren(10.0, 2.0);
		assertEquals(5.0, result);
	}

	@Test
	void divideMitNegativerZahl() {
		var result = calculator.dividieren(10.0, -2.0);
		assertEquals(-5.0, result);
	}

	@Test
	void divideMitKommaErgebnis() {
		var result = calculator.dividieren(10.0, 4.0);
		assertEquals(2.5, result);
	}
}
