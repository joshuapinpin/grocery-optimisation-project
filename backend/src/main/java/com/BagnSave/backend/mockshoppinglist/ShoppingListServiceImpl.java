package com.BagnSave.backend.mockshoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AccountRepository;
import com.BagnSave.backend.mockshoppinglist.dto.CreateListRequest;
import com.BagnSave.backend.mockshoppinglist.dto.ShoppingListResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final AccountRepository accountRepository;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository,
                                   AccountRepository accountRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public ShoppingListResponse createShoppingList(CreateListRequest request, Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        ShoppingList list = new ShoppingList()
                .name(request.name())
                .owner(account);

        ShoppingList saved = shoppingListRepository.save(list);

        List<String> mockedItems = getMockedItemsForList(saved.name());

        return new ShoppingListResponse(
                account.username(),
                List.of(
                        new ShoppingListResponse.ListSummary(
                                saved.id(),
                                saved.name(),
                                mockedItems
                        )
                )
        );
    }

    @Override
    public List<ShoppingListResponse> getShoppingListsForUser(Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        List<ShoppingList> lists = shoppingListRepository.findByOwnerUsername(account.username());

        List<ShoppingListResponse.ListSummary> summaries = lists.stream()
                .map(list -> new ShoppingListResponse.ListSummary(
                        list.id(),
                        list.name(),
                        getMockedItemsForList(list.name())
                ))
                .toList();

        return List.of(new ShoppingListResponse(account.username(), summaries));
    }

    @Override
    public void deleteShoppingList(Long listId, Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        ShoppingList list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping list not found"));

        if (!list.owner().id().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this shopping list");
        }

        shoppingListRepository.delete(list);
    }

    private List<String> getMockedItemsForList(String listName) {
        Map<String, List<String>> mockedItems = Map.of(
                "Weekly Shop", List.of("Milk", "Bread", "Eggs", "Butter"),
                "Flatmate List", List.of("Rice", "Pasta", "Canned Tomatoes")
        );
        return mockedItems.getOrDefault(listName, List.of());
    }
}
