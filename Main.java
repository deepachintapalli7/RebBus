package busbooking;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Bus bus1 = new Bus(101, "Orange Travels", "Hyderabad", "Bangalore", 40);
        Bus bus2 = new Bus(102, "VRL Travels", "Chennai", "Hyderabad", 45);
        Bus bus3 = new Bus(103, "SRS Travels", "Bangalore", "Chennai", 35);

        Booking booking = null;
        User customer = null;

        int bookingId = 1;
        int userId = 1;
        int choice = 0;

        while (choice != 5) {

            System.out.println("\n===== BUS BOOKING SYSTEM =====");
            System.out.println("1. View Buses");
            System.out.println("2. Book Ticket");
            System.out.println("3. View Booking");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {

            case 1:
                System.out.println("\n===== AVAILABLE BUSES =====");

                System.out.println("101 | Orange Travels | Hyderabad -> Bangalore | 40 seats");
                System.out.println("102 | VRL Travels    | Chennai -> Hyderabad   | 45 seats");
                System.out.println("103 | SRS Travels    | Bangalore -> Chennai   | 35 seats");
                break;

            case 2:
                System.out.println("\n===== CUSTOMER DETAILS =====");

                System.out.print("Enter your name: ");
                sc.nextLine();
                String name = sc.nextLine();

                System.out.print("Enter your age: ");
                int age = sc.nextInt();

                String phone;
                while (true) {
                    System.out.print("Enter 10-digit phone number: ");
                    phone = sc.next();

                    if (phone.matches("[6-9][0-9]{9}")) {
                        break;
                    }

                    System.out.println("Invalid phone number!");
                }

                String email;
                while (true) {
                    System.out.print("Enter email: ");
                    email = sc.next();

                    if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                        break;
                    }

                    System.out.println("Invalid email!");
                }

                customer = new User(userId, name, age, phone, email);

                System.out.println("\n===== BOOK TICKET =====");

                System.out.print("Enter bus number: ");
                int busNumber = sc.nextInt();

                if (busNumber != 101 && busNumber != 102 && busNumber != 103) {
                    System.out.println("Invalid bus number!");
                    break;
                }

                System.out.print("Enter seat number: ");
                int seatNumber = sc.nextInt();

                if (seatNumber < 1 || seatNumber > 40) {
                    System.out.println("Invalid seat number!");
                    break;
                }

                if (booking != null &&
                    booking.busNumber == busNumber &&
                    booking.seatNumber == seatNumber) {

                    System.out.println("Sorry! This seat is already booked.");
                    break;
                }

                System.out.print("Enter journey date (DD-MM-YYYY): ");
                String journeyDate = sc.next();

                booking = new Booking(
                    bookingId,
                    busNumber,
                    customer.name,
                    seatNumber,
                    journeyDate
                );

                System.out.println("\n===== BOOKING SUCCESSFUL =====");
                System.out.println("Booking ID: " + booking.bookingId);
                break;

            case 3:
                System.out.println("\n===== YOUR BOOKING =====");

                if (booking != null) {
                    System.out.println("Booking ID: " + booking.bookingId);
                    System.out.println("Customer ID: " + customer.userId);
                    System.out.println("Name: " + customer.name);
                    System.out.println("Age: " + customer.age);
                    System.out.println("Phone: " + customer.phone);
                    System.out.println("Email: " + customer.email);
                    System.out.println("Bus Number: " + booking.busNumber);
                    System.out.println("Seat Number: " + booking.seatNumber);
                    System.out.println("Journey Date: " + booking.journeyDate);
                } else {
                    System.out.println("No booking found.");
                }
                break;

            case 4:
                System.out.println("\n===== CANCEL BOOKING =====");

                if (booking != null) {
                    System.out.println("Booking ID: " + booking.bookingId);
                    System.out.print("Cancel booking? (yes/no): ");

                    sc.nextLine();
                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("yes")) {
                        booking = null;
                        customer = null;
                        System.out.println("Booking cancelled successfully!");
                    } else {
                        System.out.println("Booking not cancelled.");
                    }

                } else {
                    System.out.println("No booking found.");
                }
                break;

            case 5:
                System.out.println("\nThank you for using Bus Booking System!");
                break;

            default:
                System.out.println("Invalid choice!");
            }
        }

        sc.close();
    }
}