package com.BagnSave.backend.product.dto;

public class ProductDTO {
    
    private Integer id;
    private String name;
    private String brand;
    private String unit;
    private String size;
    private Integer redirectedTo;

    public ProductDTO() {}

    public ProductDTO(Integer id, String name, String brand, String unit, String size, Integer redirectedTo) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.unit = unit;
        this.size = size;
        this.redirectedTo = redirectedTo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getRedirectedTo() {
        return redirectedTo;
    }

    public void setRedirectedTo(Integer redirectedTo) {
        this.redirectedTo = redirectedTo;
    }

}
