package com.BagnSave.backend.shoppinglist.item;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AuthenticatedAccountResolver;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists/{listId}/items")
public class ShoppingListItemController {

    private final ShoppingListItemService shoppingListItemService;
    private final AuthenticatedAccountResolver accountResolver;

    public ShoppingListItemController(ShoppingListItemService shoppingListItemService, AuthenticatedAccountResolver accountResolver) {
        this.shoppingListItemService = shoppingListItemService;
        this.accountResolver = accountResolver;
    }

    @GetMapping
    public List<ShoppingListItemDTO> getAllItems(@PathVariable Long listId, Authentication authentication) {
        Account account = accountResolver.resolve(authentication);
        return shoppingListItemService.getItemsByShoppingList(account, listId);
    }

    @PostMapping
    public ResponseEntity<ShoppingListItemDTO> addItem(
            @PathVariable Long listId,
            @RequestBody AddItemRequestDTO itemDTO,
            Authentication authentication
    ) {
        Account account = accountResolver.resolve(authentication);
        ShoppingListItemDTO item = shoppingListItemService.addItemToShoppingList(account, listId, itemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ShoppingListItemDTO> updateItemQuantity(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            @RequestBody UpdateItemQuantityRequestDTO itemDTO,
            Authentication authentication
    ) {
        Account account = accountResolver.resolve(authentication);
        ShoppingListItemDTO updatedItem = shoppingListItemService.updateItemQuantity(account, listId, itemId, itemDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        Account account = accountResolver.resolve(authentication);
        shoppingListItemService.removeItemFromShoppingList(account, listId, itemId);
        return ResponseEntity.noContent().build();
    }
}
