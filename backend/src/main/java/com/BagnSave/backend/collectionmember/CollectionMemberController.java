package com.BagnSave.backend.collectionmember;

import com.BagnSave.backend.collectionmember.dto.CollectionMemberDTO;
import com.BagnSave.backend.shared.PaginationDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collectionmembers")
public class CollectionMemberController {
    
    private final CollectionMemberService collectionMemberService;

    public CollectionMemberController(CollectionMemberService collectionMemberService) {
        this.collectionMemberService = collectionMemberService;
    }

    @GetMapping
    public Page<CollectionMemberDTO> getCollectionMembers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return collectionMemberService.getCollectionMembers(PageRequest.of(page, safeSize));
    }
}
