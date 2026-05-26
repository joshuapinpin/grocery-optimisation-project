package com.BagnSave.backend.mockshoppinglist;

import com.BagnSave.backend.mockshoppinglist.dto.CreateListRequest;
import com.BagnSave.backend.mockshoppinglist.dto.ShoppingListResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(ShoppingListService shoppingListService) {
        this.shoppingListService = shoppingListService;
    }

    @GetMapping("/me")
    public ResponseEntity<ShoppingListResponse> getMyLists(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<ShoppingListResponse> responses = shoppingListService.getShoppingListsForUser(userId);
        
        // The service returns a list with one element containing all user's lists
        if (responses.isEmpty()) {
            return ResponseEntity.ok(new ShoppingListResponse(authentication.getName(), List.of()));
        }
        
        return ResponseEntity.ok(responses.getFirst());
    }

    @PostMapping("/create")
    public ResponseEntity<ShoppingListResponse> createList(
            @Valid @RequestBody CreateListRequest request,
            HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ShoppingListResponse response = shoppingListService.createShoppingList(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(
            @PathVariable Long id,
            HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shoppingListService.deleteShoppingList(id, userId);
        return ResponseEntity.noContent().build();
    }
}
