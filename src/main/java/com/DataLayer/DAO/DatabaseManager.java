package com.DataLayer.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static String DB_URL = "jdbc:sqlite:data/musicplayer.db";
    private static Connection connection = null;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()){
            try{
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);

                connection.createStatement().execute("PRAGMA foreign_keys = ON;");
            }catch(ClassNotFoundException ex){
                throw new SQLException("Driver JDBC non trovato", ex);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo speciale solo per i test JUnit
    public static void setTestMode(boolean isTest) {
        if (isTest) {
            DB_URL = "jdbc:sqlite::memory:"; // Database temporaneo in RAM
        }
        closeConnection(); // Forza la riapertura con il nuovo URL
    }
}