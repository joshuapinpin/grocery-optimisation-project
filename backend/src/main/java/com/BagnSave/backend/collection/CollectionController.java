package com.BagnSave.backend.collection;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.BagnSave.backend.collection.dto.CollectionDTO;
import com.BagnSave.backend.shared.PaginationDefaults;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public Page<CollectionDTO> getCollections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return collectionService.getCollections(PageRequest.of(page, safeSize));
    }
}
