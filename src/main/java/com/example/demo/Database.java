package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database extends Employee {

    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/firmms?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASSWORD = "1234";

    public Database(int employeeID, String username, String password, String role, String name, String surname) {
        super(employeeID, username, password, role, name, surname);
    }


    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    DATABASE_URL,
                    USER,
                    PASSWORD
            );
        }
        return connection;
    }

}
