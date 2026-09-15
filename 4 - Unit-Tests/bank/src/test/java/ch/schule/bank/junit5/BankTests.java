package ch.schule.bank.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ch.schule.Bank;
import ch.schule.SavingsAccount;

public class BankTests {

	private static Bank bankWithSixAccounts() {
		Bank bank = new Bank();

		for (long amount : new long[]{600000, 500000, 400000, 300000, 300000, 100000}) {
			bank.deposit(bank.createSavingsAccount(), 0, amount);
		}

		return bank;
	}

	@Test
	public void testCreate() {
		Bank bank = new Bank();

		assertEquals("S-1000", bank.createSavingsAccount());
		assertEquals("Y-1001", bank.createPromoYouthSavingsAccount());
		assertEquals("P-1002", bank.createSalaryAccount(-500000));
		assertEquals("P-1003", bank.createSalaryAccount(0));

		assertNull(bank.createSalaryAccount(1));

		bank.setAccount(new SavingsAccount("X-1"));
		assertEquals("X-1", bank.getAccount().getId());
	}

	@Test
	public void testDeposit() {
		Bank bank = new Bank();
		String id = bank.createSavingsAccount();

		assertTrue(bank.deposit(id, 0, 100000));
		assertEquals(100000, bank.getBalance(id));

		assertFalse(bank.deposit("gibt-es-nicht", 0, 100000));
		assertEquals(0, bank.getBalance("gibt-es-nicht"));
	}

	@Test
	public void testWithdraw() {
		Bank bank = new Bank();
		String id = bank.createSavingsAccount();
		bank.deposit(id, 0, 100000);

		assertTrue(bank.withdraw(id, 0, 40000));
		assertEquals(60000, bank.getBalance(id));

		assertFalse(bank.withdraw("gibt-es-nicht", 0, 100000));
	}

	@Test
	public void testBalance() {
		Bank bank = new Bank();
		bank.deposit(bank.createSavingsAccount(), 0, 100000);
		bank.deposit(bank.createSavingsAccount(), 0, 200000);

		assertEquals(-300000, bank.getBalance());
	}
}
