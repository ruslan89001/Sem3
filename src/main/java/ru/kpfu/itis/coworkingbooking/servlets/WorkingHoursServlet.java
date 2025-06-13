    package ru.kpfu.itis.coworkingbooking.servlets;

    import ru.kpfu.itis.coworkingbooking.services.WorkingHoursService;
    import ru.kpfu.itis.coworkingbooking.models.WorkingHours;
    import jakarta.servlet.*;
    import jakarta.servlet.http.*;
    import jakarta.servlet.annotation.*;
    import java.sql.Timestamp;
    import java.io.IOException;
    import java.sql.SQLException;
    import java.util.List;

    @WebServlet("/workinghours")
    public class WorkingHoursServlet extends HttpServlet {

        private WorkingHoursService workingHoursService = new WorkingHoursService();

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try {
                List<WorkingHours> workingHoursList = workingHoursService.getAllWorkingHours();
                request.setAttribute("workingHours", workingHoursList);

                request.getRequestDispatcher("/WEB-INF/jsp/workinghours.jsp").forward(request, response);
            } catch (SQLException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching working hours.");
            }
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try {
                int spaceId = Integer.parseInt(request.getParameter("space_id"));
                String dayOfWeek = request.getParameter("day_of_week");
                Timestamp startTime = Timestamp.valueOf(request.getParameter("start_time"));
                Timestamp endTime = Timestamp.valueOf(request.getParameter("end_time"));

                WorkingHours workingHours = new WorkingHours(0, spaceId, dayOfWeek, startTime, endTime);
                workingHoursService.createWorkingHours(workingHours);
                response.sendRedirect("/workinghours");
            } catch (SQLException | IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating working hours.");
            }
        }

        @Override
        protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try {
                int workingHoursId = Integer.parseInt(request.getParameter("id"));
                workingHoursService.deleteWorkingHours(workingHoursId);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (SQLException | NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting working hours.");
            }
        }
    }
