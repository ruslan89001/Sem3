package ru.kpfu.itis.coworkingbooking.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import ru.kpfu.itis.coworkingbooking.services.*;
import ru.kpfu.itis.coworkingbooking.ConnectionWrapper;

import jakarta.servlet.annotation.WebListener;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserService userService = new UserService();
        BookingService bookingService = new BookingService();
        SpaceService spaceService = new SpaceService();
        WorkingHoursService workingHoursService = new WorkingHoursService();
        ReviewService reviewService = new ReviewService();

        sce.getServletContext().setAttribute("userService", userService);
        sce.getServletContext().setAttribute("bookingService", bookingService);
        sce.getServletContext().setAttribute("spaceService", spaceService);
        sce.getServletContext().setAttribute("workingHoursService", workingHoursService);
        sce.getServletContext().setAttribute("reviewService", reviewService);

        createDatabaseTablesAndPopulate();
    }

    private void createDatabaseTablesAndPopulate() {
        Connection conn = ConnectionWrapper.getConnection();

        if (conn != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("sql/creatingDB.sql")));
                 Statement stmt = conn.createStatement()) {

                if (reader == null) {
                    throw new FileNotFoundException("Ресурс sql/creatingDB.sql не найден в classpath.");
                }

                conn.setAutoCommit(false);
                String line;
                StringBuilder sql = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sql.append(line).append(" ");
                    if (line.trim().endsWith(";")) {
                        System.out.println("Executing SQL: " + sql.toString());
                        stmt.executeUpdate(sql.toString());
                        sql.setLength(0);
                    }
                }
                conn.commit();
                System.out.println("Таблицы успешно созданы и данные вставлены!");
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                System.out.println("Ошибка при выполнении SQL-скрипта.");
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException autoCommitEx) {
                        autoCommitEx.printStackTrace();
                    }
                }
            }
        } else {
            System.out.println("Не удалось установить соединение с базой данных.");
        }
    }
}
