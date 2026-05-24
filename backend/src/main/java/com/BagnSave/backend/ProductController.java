package com.BagnSave.backend;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {

    private List<CartItem> storedShoppingList = new ArrayList<>();

    @GetMapping("/products")
    public List<Product> getProducts() {
        return List.of(
            new Product(1, "Whole Milk (2L)", "placeholder", List.of(
                new StoreLocation(1, "Woolworths", 4.0),
                new StoreLocation(2, "Pak'nSave", 3.0),
                new StoreLocation(3, "New World", 4.0)
            )),
            new Product(2, "White Bread (700g)", "placeholder", List.of(
                new StoreLocation(1, "Woolworths", 4.0),
                new StoreLocation(2, "Pak'nSave", 2.0),
                new StoreLocation(3, "New World", 3.0)
            )),
            new Product(3, "Free Range Eggs (12pk)", "placeholder", List.of(
                new StoreLocation(1, "Woolworths", 7.0),
                new StoreLocation(2, "Pak'nSave", 6.0),
                new StoreLocation(4, "Four Square", 8.0)
            )),
            new Product(4, "Apples (1kg bag)", "placeholder", List.of(
                new StoreLocation(1, "Woolworths", 5.0),
                new StoreLocation(2, "Pak'nSave", 4.0),
                new StoreLocation(3, "New World", 5.0)
            )),
            new Product(5, "Orange Juice (2L)", "placeholder", List.of(
                new StoreLocation(1, "Woolworths", 5.0),
                new StoreLocation(2, "Pak'nSave", 5.0),
                new StoreLocation(5, "Fresh Choice", 6.0)
            ))
        );
    }

    @PostMapping("/shopping-list")
    public Map<String, String> storeShoppingList(@RequestBody List<CartItem> items) {
        storedShoppingList = new ArrayList<>(items);
        return Map.of("message", "Shopping list stored! " + items.size() + " item(s) saved.");
    }

    @GetMapping("/shopping-list")
    public List<CartItem> getShoppingList() {
        return storedShoppingList;
    }
}
