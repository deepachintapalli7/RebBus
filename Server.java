package busbooking;

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {

        try {

            ServerSocket server = new ServerSocket(9092);

            System.out.println("==============================");
            System.out.println("RebBus Server Started!");
            System.out.println("Open: http://localhost:9092");
            System.out.println("==============================");

            while (true) {

                Socket socket = server.accept();

                BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
                );

                OutputStream output = socket.getOutputStream();

                String request = input.readLine();

                int contentLength = 0;

                String header;

                while ((header = input.readLine()) != null && !header.isEmpty()) {

                    if (header.toLowerCase().startsWith("content-length:")) {

                        contentLength = Integer.parseInt(
                            header.substring(15).trim()
                        );
                    }
                }

                char[] bodyChars = new char[contentLength];

                if (contentLength > 0) {
                    input.read(bodyChars);
                }

                String body = new String(bodyChars);

                if (request == null) {
                    socket.close();
                    continue;
                }

                String path = "/";

                String[] parts = request.split(" ");

                if (parts.length > 1) {
                    path = parts[1];
                }

                if (path.equals("/")) {
                    path = "/index.html";
                }

                // BOOKING REQUEST
                if (path.equals("/book")) {

                    System.out.println("Booking request received!");

                    String name = getJsonValue(body, "name");
                    int age = Integer.parseInt(
                        getJsonValue(body, "age")
                    );

                    String gender = getJsonValue(body, "gender");
                    String phone = getJsonValue(body, "phone");
                    String email = getJsonValue(body, "email");

                    int seatNumber = Integer.parseInt(
                        getJsonValue(body, "seat")
                    );

                    int busNumber = Integer.parseInt(
                        getJsonValue(body, "busNumber")
                    );

                    String journeyDate =
                        getJsonValue(body, "journeyDate");

                    int bookingId= BookingHandler.saveCustomer(
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
                        message = "🎫 Booking successful!";
                    } else {
                        message = "❌ Booking failed!";
                    }

                    byte[] response = message.getBytes("UTF-8");

                    String bookHeader =
                        "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n" +
                        "Content-Length: " + response.length + "\r\n" +
                        "Connection: close\r\n\r\n";

                    output.write(bookHeader.getBytes("UTF-8"));
                    output.write(response);
                    output.flush();

                    socket.close();
                    continue;
                }

                // WEBSITE FILES
                File file = new File("web" + path);

                if (file.exists() && !file.isDirectory()) {

                    byte[] data = readFile(file);

                    String contentType = "text/html";

                    if (path.endsWith(".css")) {
                        contentType = "text/css";
                    }
                    else if (path.endsWith(".js")) {
                        contentType = "application/javascript";
                    }

                    String fileHeader =
                        "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType +
                        "; charset=UTF-8\r\n" +
                        "Content-Length: " + data.length +
                        "\r\n" +
                        "Connection: close\r\n\r\n";

                    try {

                        output.write(fileHeader.getBytes("UTF-8"));
                        output.write(data);
                        output.flush();

                    } catch (IOException e) {

                        System.out.println(
                            "Browser closed the connection."
                        );
                    }

                } else {

                    String message = "404 - File Not Found";

                    String fileHeader =
                        "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: " +
                        message.length() +
                        "\r\n\r\n";

                    output.write(fileHeader.getBytes("UTF-8"));
                    output.write(message.getBytes("UTF-8"));
                }

                output.flush();
                socket.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // READ VALUE FROM JSON
    private static String getJsonValue(
            String json,
            String key) {

        String search =
            "\"" + key + "\":\"";

        int start = json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start = start + search.length();

        int end = json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(start, end);
    }

    // READ WEBSITE FILE
    private static byte[] readFile(File file)
            throws IOException {

        FileInputStream input =
            new FileInputStream(file);

        ByteArrayOutputStream output =
            new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];

        int length;

        while ((length = input.read(buffer)) != -1) {

            output.write(buffer, 0, length);
        }

        input.close();

        return output.toByteArray();
    }
}
