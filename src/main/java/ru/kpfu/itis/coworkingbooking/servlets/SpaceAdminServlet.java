package ru.kpfu.itis.coworkingbooking.servlets;

import ru.kpfu.itis.coworkingbooking.models.Space;
import ru.kpfu.itis.coworkingbooking.services.SpaceService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/admin/spaces")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 1024 * 1024 * 5,   // 5MB
        maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class SpaceAdminServlet extends HttpServlet {
    private SpaceService spaceService;

    @Override
    public void init() {
        spaceService = (SpaceService) getServletContext().getAttribute("spaceService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("addSpace".equals(action)) {
                handleAddSpace(request, response);
            } else if ("updateSpace".equals(action)) {
                handleUpdateSpace(request, response);
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        }
    }

    private void handleAddSpace(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {

        Part imagePart = request.getPart("image");
        String imagePath = saveImage(imagePart);

        Space newSpace = new Space(
                0,
                request.getParameter("name"),
                request.getParameter("description"),
                Double.parseDouble(request.getParameter("price")),
                request.getParameter("location"),
                imagePath,
                Boolean.parseBoolean(request.getParameter("availability"))
        );

        spaceService.createSpace(newSpace);
        response.sendRedirect(request.getContextPath() + "/admin?action=viewSpaces");
    }

    private void handleUpdateSpace(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {

        int spaceId = Integer.parseInt(request.getParameter("spaceId"));
        Part imagePart = request.getPart("image");
        Space existingSpace = spaceService.getSpaceById(spaceId);
        String imagePath = existingSpace.getImage();

        if (imagePart != null && imagePart.getSize() > 0) {
            deleteOldImage(existingSpace.getImage());
            imagePath = saveImage(imagePart);
        }

        Space updatedSpace = new Space(
                spaceId,
                request.getParameter("name"),
                request.getParameter("description"),
                Double.parseDouble(request.getParameter("price")),
                request.getParameter("location"),
                imagePath,
                Boolean.parseBoolean(request.getParameter("availability"))
        );

        spaceService.updateSpace(updatedSpace);
        response.sendRedirect(request.getContextPath() + "/admin?action=viewSpaces");
    }

    private String saveImage(Part imagePart) throws IOException {
        if (imagePart == null || imagePart.getSize() == 0) return null;

        String contentType = imagePart.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IOException("Only images are allowed.");
        }

        String fileName = UUID.randomUUID() + "_" + imagePart.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("/resources/pictures");
        Files.createDirectories(Paths.get(uploadPath));
        imagePart.write(uploadPath + File.separator + fileName);

        return "resources/pictures/" + fileName;
    }

    private void deleteOldImage(String imagePath) {
        if (imagePath == null) return;
        File oldFile = new File(getServletContext().getRealPath(imagePath));
        if (oldFile.exists()) oldFile.delete();
    }
}