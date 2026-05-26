package com.BagnSave.backend.mockshoppinglist;

import com.BagnSave.backend.mockshoppinglist.dto.CreateListRequest;
import com.BagnSave.backend.mockshoppinglist.dto.ShoppingListResponse;

import java.util.List;

public interface ShoppingListService {
    ShoppingListResponse createShoppingList(CreateListRequest request, Long userId);
    List<ShoppingListResponse> getShoppingListsForUser(Long userId);

}
