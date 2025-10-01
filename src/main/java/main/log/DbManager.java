package main.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbManager {
    private static final String URL = "jdbc:mysql://localhost:3306/tp3";
    private static final String USER = "morganeTP3";
    private static final String PASS = "morganeTP3";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

}
