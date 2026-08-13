package com.BagnSave.backend.shoppinglist.list;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.list.dto.CreateListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.RenameListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.ShoppingListDTO;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }

    @Override
    public List<ShoppingListDTO> getListsForAccount(Account account) {
        List<ShoppingList> shoppingLists = shoppingListRepository.findByAccountId(account.getId());
        return shoppingLists.stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ShoppingListDTO getList(Account account, Long listId) {
        ShoppingList shoppingList = findListOrThrow(account.getId(), listId);
        return toDTO(shoppingList);
    }

    @Override
    public ShoppingListDTO createList(Account account, CreateListRequestDTO request) {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setAccount(account);
        shoppingList.setName(request.getName());
        shoppingList = shoppingListRepository.save(shoppingList);
        return toDTO(shoppingList);
    }

    @Override
    public ShoppingListDTO renameList(Account account, Long listId, RenameListRequestDTO request) {
        ShoppingList shoppingList = findListOrThrow(account.getId(), listId);
        shoppingList.setName(request.getName());
        shoppingList = shoppingListRepository.save(shoppingList);
        return toDTO(shoppingList);
    }

    @Override
    public void deleteList(Account account, Long listId) {
        ShoppingList shoppingList = findListOrThrow(account.getId(), listId);
        shoppingListRepository.delete(shoppingList);
    }

    // --- HELPER METHODS ---

    private ShoppingList findListOrThrow(Long accountId, Long listId) {
        return shoppingListRepository.findByIdAndAccountId(listId, accountId)
                .orElseThrow(ShoppingListNotFoundException::new);
    }

    private ShoppingListDTO toDTO(ShoppingList shoppingList) {
        return new ShoppingListDTO(
                shoppingList.getId(),
                shoppingList.getAccount().getId(),
                shoppingList.getName(),
                shoppingList.getProducts().stream()
                        .map(item -> new ShoppingListItemDTO(
                                item.getId(),
                                item.getProductRef(),
                                item.getQuantity()
                        ))
                        .toList()
        );
    }
}
