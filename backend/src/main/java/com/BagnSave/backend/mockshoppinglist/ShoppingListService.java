package com.BagnSave.backend.mockshoppinglist;

import com.BagnSave.backend.mockshoppinglist.dto.CreateListRequest;
import com.BagnSave.backend.mockshoppinglist.dto.ShoppingListResponse;

import java.util.List;

public interface ShoppingListService {
    /**
     * Creates a new shopping list for the given user.
     *
     * @param request the request containing the name of the new shopping list
     * @param userId the ID of the user for whom the shopping list is being created
     * @return a response containing the created shopping list and the username of the owner
     */
    ShoppingListResponse createShoppingList(CreateListRequest request, Long userId);

    /**
     * Retrieves all shopping lists for the given user.
     * @param userId the ID of the user whose shopping lists are being retrieved
     * @return a list of responses, each containing a shopping list and the username of the owner.
     *      In this mock implementation, it will return a single response with all lists for the user.
     */
    List<ShoppingListResponse> getShoppingListsForUser(Long userId);

    /**
     * Deletes a shopping list if it belongs to the given user.
     * @param listId the ID of the shopping list to delete
     * @param userId the ID of the user attempting to delete the list
     */
    void deleteShoppingList(Long listId, Long userId);

}
