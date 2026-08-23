package com.BagnSave.backend.shoppinglist.list;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AccountService;
import com.BagnSave.backend.auth.AuthProvider;
import com.BagnSave.backend.auth.AuthenticatedAccountResolver;
import com.BagnSave.backend.auth.userdetailsservice.CustomUserDetails;
import com.BagnSave.backend.shoppinglist.list.dto.CreateListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.RenameListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.ShoppingListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;
    private final AuthenticatedAccountResolver accountResolver;

    public ShoppingListController(ShoppingListService shoppingListService, AuthenticatedAccountResolver accountResolver) {
        this.shoppingListService = shoppingListService;
        this.accountResolver = accountResolver;
    }

    @GetMapping
    public List<ShoppingListDTO> getAllShoppingLists(Authentication authentication) {
        Account account = accountResolver.resolve(authentication);
        return shoppingListService.getListsForAccount(account);
    }

    @GetMapping("/{listId}")
    public ShoppingListDTO getShoppingList(@PathVariable Long listId, Authentication authentication) {
        Account account = accountResolver.resolve(authentication);
        return shoppingListService.getList(account, listId);
    }

    @PostMapping
    public ResponseEntity<ShoppingListDTO> createShoppingList(
            @RequestBody CreateListRequestDTO request,
            Authentication authentication
    ) {
        Account account = accountResolver.resolve(authentication);
        ShoppingListDTO createdList = shoppingListService.createList(account, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ShoppingListDTO> renameShoppingList(
            @PathVariable Long listId,
            @RequestBody RenameListRequestDTO request,
            Authentication authentication
    ) {
        Account account = accountResolver.resolve(authentication);
        ShoppingListDTO renamedList = shoppingListService.renameList(account, listId, request);
        return ResponseEntity.ok(renamedList);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Long listId, Authentication authentication) {
        Account account = accountResolver.resolve(authentication);
        shoppingListService.deleteList(account, listId);
        return ResponseEntity.noContent().build();
    }
}
