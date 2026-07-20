package com.BagnSave.backend.controller;

import com.BagnSave.backend.dto.StoreDTO;
import com.BagnSave.backend.service.SupermarketService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supermarkets")
@CrossOrigin(origins = "*") // To-Do: tighten to vercel URL
public class SupermarketController {
    
    private final SupermarketService supermarketService;

    public SupermarketController(SupermarketService supermarketService) {
        this.supermarketService = supermarketService;
    }

    @GetMapping
    public List<StoreDTO> getAllStores() {
        return supermarketService.getAllStores();
    }
}
