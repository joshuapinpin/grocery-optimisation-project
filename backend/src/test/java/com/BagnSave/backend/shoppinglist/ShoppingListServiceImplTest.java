package com.BagnSave.backend.shoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.shoppinglist.exception.DuplicateListNameException;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import com.BagnSave.backend.shoppinglist.list.ShoppingList;
import com.BagnSave.backend.shoppinglist.list.ShoppingListRepository;
import com.BagnSave.backend.shoppinglist.list.ShoppingListServiceImpl;
import com.BagnSave.backend.shoppinglist.list.dto.CreateListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.RenameListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.ShoppingListDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingListServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    private ShoppingListServiceImpl service;

    private Account account;

    @BeforeEach
    void setup(){
        service = new ShoppingListServiceImpl(shoppingListRepository);
        account = new Account();
        account.setId(1L);
        account.setEmail("test@example.com");
        account.setName("Test User");
    }

    private ShoppingList newList(Long id, String name) {
        ShoppingList list = new ShoppingList();
        list.setId(id);
        list.setName(name);
        list.setAccount(account);
        return list;
    }

    // ========== READ LIST TESTS ==========

    @Test
    void getListsForAccount_returnsAllListsMappedToDTO(){
        // Setup mock repository to return a list of ShoppingList entities
        ShoppingList list1 = newList(1L, "Groceries");
        ShoppingList list2 = newList(2L, "Electronics");
        when(shoppingListRepository.findByAccountId(account.getId())).thenReturn(List.of(list1, list2));

        // Call the service method
        List<ShoppingListDTO> result = service.getListsForAccount(account);

        // Verify the result
        assertEquals(2, result.size());
        assertEquals("Groceries", result.get(0).getName());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Electronics", result.get(1).getName());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    void getListsForAccount_returnsEmptyList_whenAccountHasNoLists(){
        // Setup mock repository to return an empty list
        when(shoppingListRepository.findByAccountId(account.getId())).thenReturn(List.of());

        // Call the service method
        List<ShoppingListDTO> result = service.getListsForAccount(account);

        // Verify the result
        assertEquals(0, result.size());
    }

    @Test
    void getList_returnsDTO_whenListBelongsToAccount() {
        ShoppingList list = newList(5L, "Weekly");
        when(shoppingListRepository.findByIdAndAccountId(5L, account.getId()))
                .thenReturn(Optional.of(list));

        ShoppingListDTO dto = service.getList(account, 5L);

        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getName()).isEqualTo("Weekly");
        assertThat(dto.getAccountId()).isEqualTo(account.getId());
    }

    @Test
    void getList_throwsShoppingListNotFoundException_whenListDoesNotBelongToAccountOrMissing() {
        when(shoppingListRepository.findByIdAndAccountId(99L, account.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getList(account, 99L))
                .isInstanceOf(ShoppingListNotFoundException.class);
    }

    // ========== CREATE LIST TESTS ==========

    @Test
    void createList_savesAndReturnsDTO_whenNameIsUnique() {
        CreateListRequestDTO request = new CreateListRequestDTO("Weekly");
        when(shoppingListRepository.existsByAccountIdAndName(account.getId(), "Weekly"))
                .thenReturn(false);
        when(shoppingListRepository.save(any(ShoppingList.class)))
                .thenAnswer(invocation -> {
                    ShoppingList saved = invocation.getArgument(0);
                    saved.setId(10L);
                    return saved;
                });

        ShoppingListDTO dto = service.createList(account, request);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getName()).isEqualTo("Weekly");
        assertThat(dto.getAccountId()).isEqualTo(account.getId());
        verify(shoppingListRepository).save(any(ShoppingList.class));
    }

    @Test
    void createList_throwsDuplicateListNameException_whenNameAlreadyExistsForAccount() {
        CreateListRequestDTO request = new CreateListRequestDTO("Weekly");
        when(shoppingListRepository.existsByAccountIdAndName(account.getId(), "Weekly"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.createList(account, request))
                .isInstanceOf(DuplicateListNameException.class);

        verify(shoppingListRepository, never()).save(any());
    }

    // ============ UPDATE LIST TESTS ============

    @Test
    void renameList_updatesNameAndReturnsDTO_whenListExists() {
        ShoppingList list = newList(3L, "Old Name");
        RenameListRequestDTO request = new RenameListRequestDTO("New Name");

        when(shoppingListRepository.findByIdAndAccountId(3L, account.getId()))
                .thenReturn(Optional.of(list));
        when(shoppingListRepository.save(any(ShoppingList.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingListDTO dto = service.renameList(account, 3L, request);

        assertThat(dto.getName()).isEqualTo("New Name");
        verify(shoppingListRepository).save(list);
    }

    @Test
    void renameList_throwsShoppingListNotFoundException_whenListDoesNotExist() {
        RenameListRequestDTO request = new RenameListRequestDTO("New Name");
        when(shoppingListRepository.findByIdAndAccountId(3L, account.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renameList(account, 3L, request))
                .isInstanceOf(ShoppingListNotFoundException.class);
    }

    // ========== DELETE LIST TESTS ============

    @Test
    void deleteList_deletesList_whenListExists() {
        ShoppingList list = newList(7L, "Weekly");
        when(shoppingListRepository.findByIdAndAccountId(7L, account.getId()))
                .thenReturn(Optional.of(list));

        service.deleteList(account, 7L);

        verify(shoppingListRepository).delete(list);
    }

    @Test
    void deleteList_throwsShoppingListNotFoundException_whenListDoesNotExist() {
        when(shoppingListRepository.findByIdAndAccountId(7L, account.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteList(account, 7L))
                .isInstanceOf(ShoppingListNotFoundException.class);

        verify(shoppingListRepository, never()).delete(any());
    }
}
