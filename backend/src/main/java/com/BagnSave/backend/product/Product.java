package com.BagnSave.backend.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "public_products")
public class Product {

    @Id
    private int id;
    private String name;
    private String brand;
    private String unit;
    private String size;
    private int redirectedTo;

    public Product() {} // private ?

    public Product(Integer id, String name, String brand, String unit, String size, Integer redirectedTo) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.unit = unit;
        this.size = size;
        this.redirectedTo = redirectedTo;
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getRedirectedTo() {
        return redirectedTo;
    }

    public void setRedirectedTo(int redirectedTo) {
        this.redirectedTo = redirectedTo;
    }
}
