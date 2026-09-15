package ch.schule.bank.junit5;

import ch.schule.PromoYouthSavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PromoYouthSavingsAccountTests
{
	@Test
	public void test()
	{
		PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");

		assertTrue(account.deposit(0, 100000));
		assertEquals(101000, account.getBalance());

		assertTrue(account.deposit(0, 99));
		assertEquals(101099, account.getBalance());

		assertFalse(account.withdraw(0, 101100));
	}
}
