package busbooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookingHandler {

    public static int saveCustomer(
            String name,
            int age,
            String gender,
            String phone,
            String email,
            int busNumber,
            int seatNumber,
            String journeyDate) {

        Connection con = null;

        try {

            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            String customerSql =
                "INSERT INTO customers " +
                "(name, age, gender, phone, email) " +
                "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement customerPs =
                con.prepareStatement(
                    customerSql,
                    PreparedStatement.RETURN_GENERATED_KEYS
                );

            customerPs.setString(1, name);
            customerPs.setInt(2, age);
            customerPs.setString(3, gender);
            customerPs.setString(4, phone);
            customerPs.setString(5, email);

            customerPs.executeUpdate();

            ResultSet customerKeys =
                customerPs.getGeneratedKeys();

            if (!customerKeys.next()) {
                con.rollback();
                return -1;
            }

            int customerId = customerKeys.getInt(1);

            String bookingSql =
                "INSERT INTO bookings " +
                "(customer_id, bus_number, seat_number, journey_date) " +
                "VALUES (?, ?, ?, ?)";

            PreparedStatement bookingPs =
                con.prepareStatement(
                    bookingSql,
                    PreparedStatement.RETURN_GENERATED_KEYS
                );

            bookingPs.setInt(1, customerId);
            bookingPs.setInt(2, busNumber);
            bookingPs.setInt(3, seatNumber);
            bookingPs.setDate(
                4,
                java.sql.Date.valueOf(journeyDate)
            );

            bookingPs.executeUpdate();

            ResultSet bookingKeys =
                bookingPs.getGeneratedKeys();

            if (!bookingKeys.next()) {
                con.rollback();
                return -1;
            }

            int bookingId = bookingKeys.getInt(1);

            con.commit();

            customerKeys.close();
            bookingKeys.close();
            customerPs.close();
            bookingPs.close();
            con.close();

            System.out.println(
                "===== BOOKING SUCCESSFUL ====="
            );

            System.out.println(
                "Booking ID: " + bookingId
            );

            return bookingId;

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            System.out.println(
                "Failed to save booking!"
            );

            e.printStackTrace();

            return -1;
        }
    }
}