package ru.kpfu.itis.coworkingbooking.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "spaces")
public class Space {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Название обязательно")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Описание обязательно")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotBlank(message = "Местоположение обязательно")
    @Column(nullable = false)
    private String location;
    
    @Column(name = "image")
    private String image;
    
    @Column(nullable = false)
    private boolean availability = true;
    
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Booking> bookings;
    
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Review> reviews;
    
    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<WorkingHours> workingHours;
    
    public Space() {}
    
    public Space(String name, String description, BigDecimal price, String location, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.image = image;
        this.availability = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public boolean isAvailability() {
        return availability;
    }
    
    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
    
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    
    public List<WorkingHours> getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(List<WorkingHours> workingHours) {
        this.workingHours = workingHours;
    }
}
