package com.BagnSave.backend;

import java.util.List;

public class Product {
    private int id;
    private String name;
    private String image;
    private List<StoreLocation> locations;

    public Product() {}

    public Product(int id, String name, String image, List<StoreLocation> locations) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.locations = locations;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<StoreLocation> getLocations() { return locations; }
    public void setLocations(List<StoreLocation> locations) { this.locations = locations; }
}
