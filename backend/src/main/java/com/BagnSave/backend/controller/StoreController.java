package com.BagnSave.backend.controller;

import com.BagnSave.backend.dto.StoreDTO;
import com.BagnSave.backend.service.StoreService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@CrossOrigin(origins = "*") // To-Do: tighten to vercel URL
public class StoreController {
    
    private final StoreService storeService;

    public StoreController(StoreService supermarketService) {
        this.storeService = supermarketService;
    }

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStores();
    }
}
