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

    
}
