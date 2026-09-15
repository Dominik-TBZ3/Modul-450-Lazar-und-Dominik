package ch.schule.bank.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ch.schule.Account;
import ch.schule.Booking;

public class AccountTests {

	private static class PlainAccount extends Account {
		PlainAccount(String id) {
			super(id);
		}
	}

	@Test
	public void testInit() {
		Account account = new PlainAccount("A-1");

		assertEquals("A-1", account.getId());
		assertEquals(0, account.getBalance());
	}

	@Test
	public void testDeposit() {
		Account account = new PlainAccount("A-1");

		assertTrue(account.deposit(0, 100000));
		assertEquals(100000, account.getBalance());

		assertFalse(account.deposit(0, -1));
		assertEquals(100000, account.getBalance());
	}

	@Test
	public void testWithdraw() {
		Account account = new PlainAccount("A-1");
		account.deposit(0, 100000);

		assertTrue(account.withdraw(0, 300000));
		assertEquals(-200000, account.getBalance());

		assertFalse(account.withdraw(0, -1));
		assertFalse(account.withdraw(-1, 100000));
	}

	@Test
	public void testReferences() {
		Account account = new PlainAccount("A-1");
		account.setBooking(new Booking(0, 100000));

		assertEquals(100000, account.getBooking().getAmount());
	}

	@Test
	public void testCanTransact() {
		Account account = new PlainAccount("A-1");

		assertTrue(account.canTransact(0));

		account.deposit(10, 100000);

		assertTrue(account.canTransact(10));
		assertFalse(account.canTransact(9));
		assertFalse(account.deposit(9, 100000));
	}
}
