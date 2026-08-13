package com.BagnSave.backend.shoppinglist.item;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AccountService;
import com.BagnSave.backend.auth.AuthProvider;
import com.BagnSave.backend.auth.userdetailsservice.CustomUserDetails;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists-items")
public class ShoppingListItemController {

    private final ShoppingListItemService shoppingListItemService;
    private final AccountService accountService;

    public ShoppingListItemController(ShoppingListItemService shoppingListItemService, AccountService accountService) {
        this.shoppingListItemService = shoppingListItemService;
        this.accountService = accountService;
    }

    @GetMapping
    public List<ShoppingListItemDTO> getAllItems(@PathVariable Long listId, Authentication authentication) {
        Account account = resolveAccount(authentication);
        return shoppingListItemService.getItemsByShoppingList(account, listId);
    }

    @PostMapping
    public ResponseEntity<ShoppingListItemDTO> addItem(
            @PathVariable Long listId,
            @RequestBody AddItemRequestDTO itemDTO,
            Authentication authentication
    ) {
        Account account = resolveAccount(authentication);
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
        Account account = resolveAccount(authentication);
        ShoppingListItemDTO updatedItem = shoppingListItemService.updateItemQuantity(account, listId, itemId, itemDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedItem);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long listId,
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        Account account = resolveAccount(authentication);
        shoppingListItemService.removeItemFromShoppingList(account, listId, itemId);
        return ResponseEntity.noContent().build();
    }


    // --- HELPER METHODS ---
    private Account resolveAccount(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getAccount();
        }
        OAuth2User oauth2User = (OAuth2User) principal;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        return accountService.findOrCreateAccount(email, name, providerId, AuthProvider.GOOGLE);
    }


}
