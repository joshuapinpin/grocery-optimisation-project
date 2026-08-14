package com.BagnSave.backend.shoppinglist.list;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.shoppinglist.list.dto.CreateListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.RenameListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.ShoppingListDTO;

import java.util.List;

public interface ShoppingListService {
    List<ShoppingListDTO> getListsForAccount(Account account);
    ShoppingListDTO getList(Account account, Long listId);
    ShoppingListDTO createList(Account account, CreateListRequestDTO request);
    ShoppingListDTO renameList(Account account, Long listId, RenameListRequestDTO request);
    void deleteList(Account account, Long listId);
}
