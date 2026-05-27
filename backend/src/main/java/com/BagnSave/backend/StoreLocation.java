package com.BagnSave.backend;

public class StoreLocation {
    private int storeId;
    private String storeName;
    private double price;

    public StoreLocation() {}

    public StoreLocation(int storeId, String storeName, double price) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.price = price;
    }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
