package ch.schule.bank.junit5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import ch.schule.Booking;

public class BookingTests
{
	@Test
	public void testInitialization()
	{
		Booking booking = new Booking(13576, 12000);

		assertEquals(13576, booking.getDate());
		assertEquals(12000, booking.getAmount());
	}
}
