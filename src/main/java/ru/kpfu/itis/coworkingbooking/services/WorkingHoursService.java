package ru.kpfu.itis.coworkingbooking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.coworkingbooking.models.WorkingHours;
import ru.kpfu.itis.coworkingbooking.repositories.WorkingHoursRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WorkingHoursService {

    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    public List<WorkingHours> getAllWorkingHours() {
        return workingHoursRepository.findAll();
    }

    public Optional<WorkingHours> getWorkingHoursById(Long id) {
        return workingHoursRepository.findById(id);
    }

    public WorkingHours createWorkingHours(WorkingHours workingHours) {
        return workingHoursRepository.save(workingHours);
    }

    public WorkingHours updateWorkingHours(WorkingHours workingHours) {
        return workingHoursRepository.save(workingHours);
    }

    public void deleteWorkingHours(Long id) {
        workingHoursRepository.deleteById(id);
    }

    public List<WorkingHours> findBySpaceId(Long spaceId) {
        return workingHoursRepository.findBySpaceId(spaceId);
    }
}
