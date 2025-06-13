package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import ru.kpfu.itis.coworkingbooking.models.Space;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/spaces")
public class SpaceServlet extends HttpServlet {

    private SpaceService spaceService = new SpaceService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<Space> spaces = spaceService.getAllSpaces();
            request.setAttribute("spaces", spaces);
            request.getRequestDispatcher("/WEB-INF/jsp/spaces.jsp").forward(request, response);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка при получении списка пространств.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            String location = request.getParameter("location");
            String image = request.getParameter("image");
            boolean availability = Boolean.parseBoolean(request.getParameter("availability"));

            Space space = new Space(0, name, description, price, location, image, availability);
            spaceService.createSpace(space);
            response.sendRedirect("/spaces");
        } catch (SQLException | NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating space.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int spaceId = Integer.parseInt(request.getParameter("id"));
            spaceService.deleteSpace(spaceId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException | NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting space.");
        }
    }
}
