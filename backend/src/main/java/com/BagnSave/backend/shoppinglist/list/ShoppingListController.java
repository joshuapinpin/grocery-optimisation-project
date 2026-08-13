package com.BagnSave.backend.shoppinglist.list;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AccountService;
import com.BagnSave.backend.auth.AuthProvider;
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
    private final AccountService accountService;

    public ShoppingListController(ShoppingListService shoppingListService, AccountService accountService) {
        this.shoppingListService = shoppingListService;
        this.accountService = accountService;
    }

    @GetMapping
    public List<ShoppingListDTO> getAllShoppingLists(Authentication authentication) {
        Account account = resolveAccount(authentication);
        return shoppingListService.getListsForAccount(account);
    }

    @GetMapping("/{listId}")
    public ShoppingListDTO getShoppingList(@PathVariable Long listId, Authentication authentication) {
        Account account = resolveAccount(authentication);
        return shoppingListService.getList(account, listId);
    }

    @PostMapping
    public ResponseEntity<ShoppingListDTO> createShoppingList(
            @RequestBody CreateListRequestDTO request,
            Authentication authentication
    ) {
        Account account = resolveAccount(authentication);
        ShoppingListDTO createdList = shoppingListService.createList(account, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ShoppingListDTO> renameShoppingList(
            @PathVariable Long listId,
            @RequestBody RenameListRequestDTO request,
            Authentication authentication
    ) {
        Account account = resolveAccount(authentication);
        ShoppingListDTO renamedList = shoppingListService.renameList(account, listId, request);
        return ResponseEntity.ok(renamedList);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteShoppingList(@PathVariable Long listId, Authentication authentication) {
        Account account = resolveAccount(authentication);
        shoppingListService.deleteList(account, listId);
        return ResponseEntity.noContent().build();
    }

    // --- HELPER METHODS ---

    private Account resolveAccount(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        // Check if the account is from manual authentication (CustomUserDetails)
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getAccount();
        }

        // If not, it must be from OAuth2 authentication (OAuth2User)
        OAuth2User oauth2User = (OAuth2User) principal;
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String providerId = oauth2User.getAttribute("sub");
        return accountService.findOrCreateAccount(email, name, providerId, AuthProvider.GOOGLE);
    }
}
