package com.unab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://bx45mlps31atehpv35ca-mysql.services.clever-cloud.com:3306/bx45mlps31atehpv35ca";
    private static final String USER = "utwtrbkwhjzwg7s9";
    private static final String PASSWORD = "4I1cluKAqoH83Sxe2GiD";
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
} 