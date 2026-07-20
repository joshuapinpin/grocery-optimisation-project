package com.BagnSave.backend.dto;

public class StoreDTO {
    
    private int id;
    private String name;
    private boolean isEnabled;
    private String vendorName;

    public StoreDTO() {}

    public StoreDTO(int id, String name, boolean isEnabled, String vendorName) {
        this.id = id;
        this.name = name;
        this.isEnabled = isEnabled;
        this.vendorName = vendorName;
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
    
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
