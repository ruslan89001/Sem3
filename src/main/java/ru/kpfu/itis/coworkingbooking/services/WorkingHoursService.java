package ru.kpfu.itis.coworkingbooking.services;

import ru.kpfu.itis.coworkingbooking.dao.WorkingHoursDAO;
import ru.kpfu.itis.coworkingbooking.models.WorkingHours;

import java.sql.SQLException;
import java.util.List;

public class WorkingHoursService {

    private WorkingHoursDAO workingHoursDAO = new WorkingHoursDAO();

    public List<WorkingHours> getAllWorkingHours() throws SQLException {
        return workingHoursDAO.getAllWorkingHours();
    }

    public WorkingHours getWorkingHoursById(int id) throws SQLException {
        return workingHoursDAO.getWorkingHoursById(id);
    }

    public void createWorkingHours(WorkingHours workingHours) throws SQLException {
        workingHoursDAO.createWorkingHours(workingHours);
    }

    public void updateWorkingHours(WorkingHours workingHours) throws SQLException {
        workingHoursDAO.updateWorkingHours(workingHours);
    }

    public void deleteWorkingHours(int id) throws SQLException {
        workingHoursDAO.deleteWorkingHours(id);
    }
}
