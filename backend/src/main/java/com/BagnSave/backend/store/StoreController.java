package com.BagnSave.backend.store;

import com.BagnSave.backend.store.dto.StoreDTO;
import com.BagnSave.backend.shared.PaginationDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {
    
    private final StoreService storeService;

    public StoreController(StoreService supermarketService) {
        this.storeService = supermarketService;
    }

    @GetMapping
    public Page<StoreDTO> getStores(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return storeService.getStores(PageRequest.of(page, safeSize));
    }

    @GetMapping("/all")
    public List<StoreDTO> getAllStores() {
        return storeService.getAllStores();
    }
}