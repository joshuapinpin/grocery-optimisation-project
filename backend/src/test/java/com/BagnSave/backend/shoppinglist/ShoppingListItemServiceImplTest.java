package com.BagnSave.backend.shoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.product.ProductRepository;
import com.BagnSave.backend.shoppinglist.exception.ItemNotFoundException;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import com.BagnSave.backend.shoppinglist.item.ShoppingListItem;
import com.BagnSave.backend.shoppinglist.item.ShoppingListItemServiceImpl;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;
import com.BagnSave.backend.shoppinglist.list.ShoppingList;
import com.BagnSave.backend.shoppinglist.list.ShoppingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private ProductRepository productRepository;

    private ShoppingListItemServiceImpl service;

    private Account account;
    private ShoppingList list;

    @BeforeEach
    void setUp() {
        service = new ShoppingListItemServiceImpl(shoppingListRepository, productRepository);
        account = new Account();
        account.setId(1L);

        list = new ShoppingList();
        list.setId(20L);
        list.setAccount(account);
        list.setName("Weekly");
        // items defaults to new ArrayList<>() per entity definition
    }

    private ShoppingListItem newItem(Long id, String productRef, int quantity) {
        ShoppingListItem item = new ShoppingListItem();
        item.setId(id);
        item.setShoppingList(list);
        item.setProductRef(productRef);
        item.setQuantity(quantity);
        return item;
    }

    // ========== READ ITEM TESTS ==========

    @Test
    void getItemsByShoppingList_returnsMappedItems() {
        list.getItems().add(newItem(1L, "100", 2));
        list.getItems().add(newItem(2L, "200", 1));
        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));

        List<ShoppingListItemDTO> result = service.getItemsByShoppingList(account, 20L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductRef()).isEqualTo("100");
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void getItemsByShoppingList_throwsShoppingListNotFoundException_whenListNotOwnedByAccount() {
        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getItemsByShoppingList(account, 20L))
                .isInstanceOf(ShoppingListNotFoundException.class);
    }

    // ========== CREATE (ADD) ITEMS TESTS ==========

    @Test
    void addItemToShoppingList_addsAndSavesItem_whenProductExists() {
        AddItemRequestDTO request = new AddItemRequestDTO("100", 3);

        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));
        when(productRepository.existsById(100)).thenReturn(true);
        when(shoppingListRepository.save(any(ShoppingList.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingListItemDTO dto = service.addItemToShoppingList(account, 20L, request);

        assertThat(dto.getProductRef()).isEqualTo("100");
        assertThat(dto.getQuantity()).isEqualTo(3);
        assertThat(list.getItems()).hasSize(1);
        verify(shoppingListRepository).save(list);
    }

    @Test
    void addItemToShoppingList_throwsItemNotFoundException_whenProductDoesNotExist() {
        AddItemRequestDTO request = new AddItemRequestDTO("999", 1);

        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));
        when(productRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> service.addItemToShoppingList(account, 20L, request))
                .isInstanceOf(ItemNotFoundException.class);

        verify(shoppingListRepository, never()).save(any());
    }

    @Test
    void addItemToShoppingList_throwsShoppingListNotFoundException_whenListNotOwnedByAccount() {
        AddItemRequestDTO request = new AddItemRequestDTO("100", 1);
        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addItemToShoppingList(account, 20L, request))
                .isInstanceOf(ShoppingListNotFoundException.class);
    }

    // ========== UPDATE ITEM TESTS ==========

    @Test
    void updateItemQuantity_updatesQuantityAndSaves_whenItemExists() {
        ShoppingListItem item = newItem(5L, "100", 1);
        list.getItems().add(item);
        UpdateItemQuantityRequestDTO request = new UpdateItemQuantityRequestDTO(9);

        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));
        when(shoppingListRepository.save(any(ShoppingList.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShoppingListItemDTO dto = service.updateItemQuantity(account, 20L, 5L, request);

        assertThat(dto.getQuantity()).isEqualTo(9);
        assertThat(item.getQuantity()).isEqualTo(9);
        verify(shoppingListRepository).save(list);
    }

    @Test
    void updateItemQuantity_throwsItemNotFoundException_whenItemNotInList() {
        UpdateItemQuantityRequestDTO request = new UpdateItemQuantityRequestDTO(9);
        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));

        assertThatThrownBy(() -> service.updateItemQuantity(account, 20L, 999L, request))
                .isInstanceOf(ItemNotFoundException.class);
    }

    // ========== DELETE ITEM TESTS ==========

    @Test
    void removeItemFromShoppingList_removesItemAndSaves_whenItemExists() {
        ShoppingListItem item = newItem(5L, "100", 1);
        list.getItems().add(item);

        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));
        when(shoppingListRepository.save(any(ShoppingList.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.removeItemFromShoppingList(account, 20L, 5L);

        assertThat(list.getItems()).isEmpty();
        verify(shoppingListRepository).save(list);
    }

    @Test
    void removeItemFromShoppingList_throwsItemNotFoundException_whenItemNotInList() {
        when(shoppingListRepository.findByIdAndAccountId(20L, account.getId()))
                .thenReturn(Optional.of(list));

        assertThatThrownBy(() -> service.removeItemFromShoppingList(account, 20L, 999L))
                .isInstanceOf(ItemNotFoundException.class);

        verify(shoppingListRepository, never()).save(any());
    }
}