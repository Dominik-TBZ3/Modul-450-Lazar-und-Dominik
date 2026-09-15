package ch.schule.bank.junit5;

import ch.schule.SalaryAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SalaryAccountTests
{
	@Test
	public void test()
	{
		SalaryAccount account = new SalaryAccount("P-1000", -100000);

		assertTrue(account.withdraw(0, 100000));
		assertEquals(-100000, account.getBalance());

		assertFalse(account.withdraw(0, 1));
		assertEquals(-100000, account.getBalance());
	}
}
