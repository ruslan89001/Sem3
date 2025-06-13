package ru.kpfu.itis.coworkingbooking.models;

public class Space {
    private int id;
    private String name;
    private String description;
    private double price;
    private String location;
    private String image;
    private boolean availability;

    public Space(int id, String name, String description, double price, String location, String image, boolean availability) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.location = location;
        this.image = image;
        this.availability = availability;
    }


    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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
}
