package com.BagnSave.backend.shoppinglist.item;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;

import java.util.List;

public interface ShoppingListItemService {
    List<ShoppingListItemDTO> getItemsByShoppingList(Account account, Long shoppingListId);
    ShoppingListItemDTO addItemToShoppingList(Account account, Long shoppingListId, AddItemRequestDTO request);
    ShoppingListItemDTO updateItemQuantity(Account account, Long shoppingListId,
                                           Long itemId, UpdateItemQuantityRequestDTO request);
    void removeItemFromShoppingList(Account account, Long shoppingListId, Long itemId);
}
