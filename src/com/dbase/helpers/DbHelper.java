package com.dbase.helpers;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class DbHelper {

    public static Connection connection;

    public static void openConnection() {
        try {
            connection = getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void commitRollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setAutoCommit(boolean status) {
        try {
            connection.setAutoCommit(status);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
