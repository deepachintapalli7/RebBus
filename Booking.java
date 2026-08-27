package busbooking;

public class Booking {
	int bookingId;
	int busNumber;
	String passengerName;
	int seatNumber;
	String journeyDate;
	
    Booking(int bookingId, int busNumber, String passengerName, int seatNumber,String journeyDate) {
        this.bookingId = bookingId;
        this.busNumber = busNumber;
        this.passengerName = passengerName;
        this.seatNumber = seatNumber;
        this.journeyDate = journeyDate;

    }
}
