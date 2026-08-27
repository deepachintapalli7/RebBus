package busbooking;

public class Bus {
	int busNumber;
	String busName;
	String source;
	String destination;
	int totalSeats;
	
    Bus(int busNumber, String busName, String source, String destination, int totalSeats) {
        this.busNumber = busNumber;
        this.busName = busName;
        this.source = source;
        this.destination = destination;
        this.totalSeats = totalSeats;
    }

}
