package com.BagnSave.backend.mockshoppinglist;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lists")
public class MockListController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyLists() {
        // Extract username from the authenticated session
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Return mock lists for the authenticated user
        Map<String, Object> response = Map.of(
            "username", username,
            "lists", List.of(
                Map.of(
                    "listId", 1,
                    "listName", "Weekly Shop",
                    "items", List.of("Milk", "Bread", "Eggs", "Butter")
                ),
                Map.of(
                    "listId", 2,
                    "listName", "Flatmate List",
                    "items", List.of("Rice", "Pasta", "Canned Tomatoes")
                )
            )
        );

        return ResponseEntity.ok(response);
    }
}

