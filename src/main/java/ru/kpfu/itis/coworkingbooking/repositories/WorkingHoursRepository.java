package ru.kpfu.itis.coworkingbooking.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.coworkingbooking.models.WorkingHours;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
    
    List<WorkingHours> findBySpaceId(Long spaceId);
    
    List<WorkingHours> findBySpaceIdAndWeekday(Long spaceId, String weekday);
    
    List<WorkingHours> findByWeekday(String weekday);

    @Query("SELECT wh FROM WorkingHours wh WHERE wh.space.id = :spaceId " +
           "AND wh.startTime <= :time AND wh.endTime >= :time")
    List<WorkingHours> findSpacesOpenAtTime(@Param("spaceId") Long spaceId, 
                                           @Param("time") LocalTime time);
    
    @Query("SELECT wh FROM WorkingHours wh WHERE wh.space.id = :spaceId " +
           "AND wh.weekday = :weekday AND wh.startTime <= :startTime AND wh.endTime >= :endTime")
    List<WorkingHours> findSpacesAvailableForTimeRange(@Param("spaceId") Long spaceId, 
                                                      @Param("weekday") String weekday,
                                                      @Param("startTime") LocalTime startTime, 
                                                      @Param("endTime") LocalTime endTime);
} 