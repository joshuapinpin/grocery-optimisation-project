package com.BagnSave.backend.collectionhierarchy;

import com.BagnSave.backend.collectionhierarchy.dto.CollectionHierarchyDTO;
import com.BagnSave.backend.shared.PaginationDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collectionhierarchy")
public class CollectionHierarchyController {
    
    private final CollectionHierarchyService collectionHierarchyService;

    public CollectionHierarchyController(CollectionHierarchyService collectionHierarchyService) {
        this.collectionHierarchyService = collectionHierarchyService;
    }

    @GetMapping
    public Page<CollectionHierarchyDTO> getCollectionHierarchy(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return collectionHierarchyService.getCollectionHierarchy(PageRequest.of(page, safeSize));
    }

}
