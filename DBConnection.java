package busbooking;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {

        Connection con = null;

        try {
            String host = System.getenv("DB_HOST");
            String port = System.getenv().getOrDefault("DB_PORT", "3306");
            String database = System.getenv("DB_NAME");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                       + "?sslMode=REQUIRED";

            con = DriverManager.getConnection(url, user, password);

            System.out.println("Database connected successfully!");

        } catch (Exception e) {
            System.out.println("Database connection failed!");
            e.printStackTrace();
        }

        return con;
    }
}
