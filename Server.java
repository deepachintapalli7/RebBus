package busbooking;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Server {

    // Stores OTP temporarily
    private static final Map<String, OTPData> otpStore =
            new ConcurrentHashMap<String, OTPData>();

    private static final SecureRandom random =
            new SecureRandom();

    public static void main(String[] args) {

        try {

            int port = Integer.parseInt(
                System.getenv().getOrDefault("PORT", "9092")
            );

            ServerSocket serverSocket =
                    new ServerSocket(port);

            System.out.println("==============================");
            System.out.println("BookMyRoute Server Started!");
            System.out.println("Open: http://localhost:9092");
            System.out.println("==============================");

            while (true) {

                Socket socket = serverSocket.accept();

                BufferedReader input =
                        new BufferedReader(
                            new InputStreamReader(
                                socket.getInputStream()
                            )
                        );

                OutputStream output =
                        socket.getOutputStream();

                String request = input.readLine();

                int contentLength = 0;

                String header;

                while ((header = input.readLine()) != null
                        && !header.isEmpty()) {

                    if (header.toLowerCase()
                            .startsWith("content-length:")) {

                        contentLength =
                                Integer.parseInt(
                                    header.substring(15).trim()
                                );
                    }
                }

                char[] bodyChars =
                        new char[contentLength];

                if (contentLength > 0) {
                    input.read(bodyChars);
                }

                String body =
                        new String(bodyChars);

                if (request == null) {
                    socket.close();
                    continue;
                }

                String path = "/";

                String[] parts =
                        request.split(" ");

                if (parts.length > 1) {
                    path = parts[1];
                }

                if (path.equals("/")) {
                    path = "/index.html";
                }


                // ==========================================
                // SEND OTP
                // ==========================================

                if (path.equals("/send-otp")) {

                    String email =
                            getJsonValue(body, "email");

                    System.out.println(
                        "OTP requested for: " + email
                    );

                    if (!email.toLowerCase()
                            .endsWith("@gmail.com")) {

                        sendTextResponse(
                            output,
                            "INVALID_EMAIL"
                        );

                        socket.close();
                        continue;
                    }

                    String otp = generateOTP();

                    otpStore.put(
                        email.toLowerCase(),
                        new OTPData(otp)
                    );

                    boolean sent =
                            sendOTPEmail(email, otp);

                    if (sent) {

                        sendTextResponse(
                            output,
                            "OTP_SENT"
                        );

                    } else {

                        otpStore.remove(
                            email.toLowerCase()
                        );

                        sendTextResponse(
                            output,
                            "EMAIL_FAILED"
                        );
                    }

                    socket.close();
                    continue;
                }


                // ==========================================
                // VERIFY OTP
                // ==========================================

                if (path.equals("/verify-otp")) {

                    String email =
                            getJsonValue(body, "email");

                    String otp =
                            getJsonValue(body, "otp");

                    OTPData saved =
                            otpStore.get(
                                email.toLowerCase()
                            );

                    if (saved == null) {

                        sendTextResponse(
                            output,
                            "INVALID"
                        );

                        socket.close();
                        continue;
                    }


                    // OTP expires after 5 minutes

                    if (System.currentTimeMillis()
                            > saved.expiryTime) {

                        otpStore.remove(
                            email.toLowerCase()
                        );

                        sendTextResponse(
                            output,
                            "EXPIRED"
                        );

                        socket.close();
                        continue;
                    }


                    if (saved.otp.equals(otp)) {

                        otpStore.remove(
                            email.toLowerCase()
                        );

                        System.out.println(
                            "OTP verified for: " + email
                        );

                        sendTextResponse(
                            output,
                            "SUCCESS"
                        );

                    } else {

                        sendTextResponse(
                            output,
                            "INVALID"
                        );
                    }

                    socket.close();
                    continue;
                }


                // ==========================================
                // EXISTING BOOKING REQUEST
                // ==========================================

                if (path.equals("/book")) {

                    System.out.println(
                        "Booking request received!"
                    );

                    String name =
                            getJsonValue(body, "name");

                    int age =
                            Integer.parseInt(
                                getJsonValue(body, "age")
                            );

                    String gender =
                            getJsonValue(body, "gender");

                    String phone =
                            getJsonValue(body, "phone");

                    String email =
                            getJsonValue(body, "email");

                    int seatNumber =
                            Integer.parseInt(
                                getJsonValue(body, "seat")
                            );

                    int busNumber =
                            Integer.parseInt(
                                getJsonValue(body, "busNumber")
                            );

                    String journeyDate =
                            getJsonValue(
                                body,
                                "journeyDate"
                            );

                    int bookingId =
                            BookingHandler.saveCustomer(
                                name,
                                age,
                                gender,
                                phone,
                                email,
                                busNumber,
                                seatNumber,
                                journeyDate
                            );

                    String message;

                    if (bookingId != -1) {

                        message =
                            "🎫 Booking successful!";

                    } else {

                        message =
                            "❌ Booking failed!";
                    }

                    byte[] response =
                            message.getBytes("UTF-8");

                    String bookHeader =
                            "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain; charset=UTF-8\r\n" +
                            "Content-Length: " +
                            response.length + "\r\n" +
                            "Connection: close\r\n\r\n";

                    output.write(
                        bookHeader.getBytes("UTF-8")
                    );

                    output.write(response);
                    output.flush();

                    socket.close();
                    continue;
                }


                // ==========================================
                // WEBSITE FILES
                // ==========================================

                File file =
                        new File("." + path);

                if (file.exists()
                        && !file.isDirectory()) {

                    byte[] data =
                            readFile(file);

                    String contentType =
                            "text/html";

                    if (path.endsWith(".css")) {

                        contentType =
                                "text/css";

                    } else if (path.endsWith(".js")) {

                        contentType =
                                "application/javascript";
