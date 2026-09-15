package ch.schule.bank.junit5;

import ch.schule.Account;
import ch.schule.AccountBalanceComparator;
import ch.schule.AccountInverseBalanceComparator;
import ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountComparatorTests
{
	private static Account account(long balance)
	{
		SavingsAccount account = new SavingsAccount("S-" + balance);
		account.deposit(0, balance);

		return account;
	}

	@Test
	public void testBalanceComparator()
	{
		AccountBalanceComparator comparator = new AccountBalanceComparator();

		assertEquals(-1, comparator.compare(account(200000), account(100000)));
		assertEquals(1, comparator.compare(account(100000), account(200000)));
		assertEquals(0, comparator.compare(account(100000), account(100000)));
	}

	@Test
	public void testInverseBalanceComparator()
	{
		AccountInverseBalanceComparator comparator = new AccountInverseBalanceComparator();

		assertEquals(-1, comparator.compare(account(100000), account(200000)));
		assertEquals(1, comparator.compare(account(200000), account(100000)));
		assertEquals(0, comparator.compare(account(100000), account(100000)));
	}
}
