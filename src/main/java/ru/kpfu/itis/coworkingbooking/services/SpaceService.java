package ru.kpfu.itis.coworkingbooking.services;

import ru.kpfu.itis.coworkingbooking.dao.SpaceDAO;
import ru.kpfu.itis.coworkingbooking.models.Space;

import java.sql.SQLException;
import java.util.List;

public class SpaceService {

    private SpaceDAO spaceDAO = new SpaceDAO();

    public List<Space> getAllSpaces() throws SQLException {
        return spaceDAO.getAllSpaces();
    }

    public Space getSpaceById(int id) throws SQLException {
        return spaceDAO.getSpaceById(id);
    }

    public void createSpace(Space space) throws SQLException {
        spaceDAO.createSpace(space);
    }

    public void updateSpace(Space space) throws SQLException {
        spaceDAO.updateSpace(space);
    }

    public void deleteSpace(int id) throws SQLException {
        spaceDAO.deleteSpace(id);
    }
}
