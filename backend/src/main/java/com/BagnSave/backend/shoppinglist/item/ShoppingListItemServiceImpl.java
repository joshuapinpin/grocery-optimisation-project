package com.BagnSave.backend.shoppinglist.item;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.product.ProductRepository;
import com.BagnSave.backend.shoppinglist.exception.ItemNotFoundException;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;
import com.BagnSave.backend.shoppinglist.list.ShoppingList;
import com.BagnSave.backend.shoppinglist.list.ShoppingListRepository;

import java.util.List;

public class ShoppingListItemServiceImpl implements ShoppingListItemService{

    private final ShoppingListRepository shoppingListRepository;
    private final ProductRepository productRepository;

    public ShoppingListItemServiceImpl(ShoppingListRepository shoppingListRepository, ProductRepository productRepository) {
        this.shoppingListRepository = shoppingListRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<ShoppingListItemDTO> getItemsByShoppingList(Account account, Long shoppingListId) {
        ShoppingList shoppingList = findListOrThrow(account, shoppingListId);
        return shoppingList.getItems().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ShoppingListItemDTO addItemToShoppingList(Account account, Long shoppingListId, AddItemRequestDTO request) {
        ShoppingList shoppingList = findListOrThrow(account, shoppingListId);

        // Check if the product exists
        if (!productRepository.existsById(Integer.parseInt(request.getProductRef()))) {
            throw new ItemNotFoundException("Product not found with reference: " + request.getProductRef());
        }

        // Create a new ShoppingListItem
        ShoppingListItem newItem = new ShoppingListItem();
        newItem.setProductRef(request.getProductRef());
        newItem.setQuantity(request.getQuantity());
        newItem.setShoppingList(shoppingList);

        // Add the item to the shopping list
        shoppingList.getItems().add(newItem);
        shoppingListRepository.save(shoppingList);

        return toDTO(newItem);
    }

    @Override
    public ShoppingListItemDTO updateItemQuantity(Account account, Long shoppingListId, Long itemId, UpdateItemQuantityRequestDTO request) {
        ShoppingList list = findListOrThrow(account, shoppingListId);
        ShoppingListItem item = findItemOrThrow(account, list, itemId);

        item.setQuantity(request.getQuantity());
        shoppingListRepository.save(list);

        return toDTO(item);
    }

    @Override
    public void removeItemFromShoppingList(Account account, Long shoppingListId, Long itemId) {
        ShoppingList list = findListOrThrow(account, shoppingListId);
        ShoppingListItem item = findItemOrThrow(account, list, itemId);

        list.getItems().remove(item);
        shoppingListRepository.save(list);
    }

    // === HELPER METHODS ===

    private ShoppingList findListOrThrow(Account account, Long shoppingListId) {
        // Implement the logic to find the shopping list by account and shoppingListId
        // If not found, throw an appropriate exception (e.g., ShoppingListNotFoundException)
        return shoppingListRepository.findByIdAndAccountId(shoppingListId, account.getId())
                .orElseThrow(ShoppingListNotFoundException::new);
    }

    private ShoppingListItem findItemOrThrow(Account account, ShoppingList shoppingList, Long itemId) {
        // Implement the logic to find the item by shoppingList and itemId
        // If not found, throw an appropriate exception (e.g., ItemNotFoundException)
        return shoppingList.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(ItemNotFoundException::new);
    }

    private ShoppingListItemDTO toDTO(ShoppingListItem item) {
        return new ShoppingListItemDTO(
                item.getId(),
                item.getProductRef(),
                item.getQuantity()
        );
    }
}
