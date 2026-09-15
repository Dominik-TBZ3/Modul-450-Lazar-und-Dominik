package ch.schule.bank.junit5;

import ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SavingsAccountTests
{
	@Test
	public void test()
	{
		SavingsAccount account = new SavingsAccount("S-1000");
		account.deposit(0, 100000);

		assertFalse(account.withdraw(0, 100001));
		assertEquals(100000, account.getBalance());

		assertTrue(account.withdraw(0, 100000));
		assertEquals(0, account.getBalance());
	}
}
