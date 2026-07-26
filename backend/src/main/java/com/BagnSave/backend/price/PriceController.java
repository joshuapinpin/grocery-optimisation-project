package com.BagnSave.backend.price;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.BagnSave.backend.price.dto.PriceDTO;
import java.util.List;

@RestController
@RequestMapping("/api/prices")
public class PriceController {
    
    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public List<PriceDTO> getAllPrices() {
        return priceService.getAllPrices();
    }
}
