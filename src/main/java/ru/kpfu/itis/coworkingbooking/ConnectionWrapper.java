package ru.kpfu.itis.coworkingbooking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionWrapper {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/coworking",
                    "postgres",
                    "12345678"
            );
            System.out.println("Соединение с базой данных установлено успешно.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Ошибка при подключении к базе данных.");
        }
        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Соединение с базой данных закрыто.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Ошибка при закрытии соединения с базой данных.");
            }
        }
    }
}
