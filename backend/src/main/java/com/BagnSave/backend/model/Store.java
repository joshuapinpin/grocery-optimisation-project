package com.BagnSave.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Store {

    // To-Do: Finalize supermarket entity data types.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private boolean isEnabled;
    private int vendorId;    

    public Store(){}
    
    public Store(int id, String name, boolean isEnabled, int vendorId){
        this.id = id;
        this.name = name;
        this.isEnabled = isEnabled;
        this.vendorId = vendorId;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id; 
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public boolean getIsEnabled(){
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled){
        this.isEnabled = isEnabled;
    }
    
    public int getVendorId(){
        return vendorId;
    }

    public void setVendorId(int vendorId){
        this.vendorId = vendorId;
    }
}
