package ru.kpfu.itis.coworkingbooking.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Entity
@Table(name = "working_hours")
public class WorkingHours {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Пространство обязательно")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    @JsonBackReference
    private Space space;
    
    @NotNull(message = "День недели обязателен")
    @Column(name = "weekday", nullable = false)
    private String weekday;
    
    @NotNull(message = "Время начала обязательно")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @NotNull(message = "Время окончания обязательно")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    public WorkingHours() {}
    
    public WorkingHours(Space space, String weekday, LocalTime startTime, LocalTime endTime) {
        this.space = space;
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Space getSpace() {
        return space;
    }
    
    public void setSpace(Space space) {
        this.space = space;
    }
    
    public String getWeekday() {
        return weekday;
    }
    
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
