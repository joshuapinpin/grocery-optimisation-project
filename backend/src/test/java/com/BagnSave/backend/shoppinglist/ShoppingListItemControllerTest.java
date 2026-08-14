package com.BagnSave.backend.shoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AuthenticatedAccountResolver;
import com.BagnSave.backend.shoppinglist.exception.ItemNotFoundException;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import com.BagnSave.backend.shoppinglist.item.ShoppingListItemController;
import com.BagnSave.backend.shoppinglist.item.ShoppingListItemService;
import com.BagnSave.backend.shoppinglist.item.dto.AddItemRequestDTO;
import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import com.BagnSave.backend.shoppinglist.item.dto.UpdateItemQuantityRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListItemControllerTest {
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @Mock
    private AuthenticatedAccountResolver accountResolver;

    @InjectMocks
    private ShoppingListItemController shoppingListItemController;

    private Account account;
    private Authentication authentication;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(shoppingListItemController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        account = new Account();
        account.setId(1L);
        authentication = new UsernamePasswordAuthenticationToken("user@example.com", "pw");
        when(accountResolver.resolve(any(Authentication.class))).thenReturn(account);
    }

    // ========== READ (GET) ITEM TESTS ==========

    @Test
    void getAllItems_returns200AndItemList() throws Exception {
        // Prepare mock data
        List<ShoppingListItemDTO> items = List.of(
                new ShoppingListItemDTO(1L, "Milk", 2),
                new ShoppingListItemDTO(2L, "Bread", 1)
        );

        // Mock the service to return the items for the given shopping list ID
        when(shoppingListItemService.getItemsByShoppingList(account, 20L)).thenReturn(items);

        mockMvc.perform(get("/api/shopping-lists/{listId}/items", 20L).principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productRef").value("Milk"))
                .andExpect(jsonPath("$[1].productRef").value("Bread"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[1].quantity").value(1));

        verify(shoppingListItemService).getItemsByShoppingList(account, 20L);
    }

    @Test
    void getAllItems_returns404_whenListNotFound() throws Exception {
        when(shoppingListItemService.getItemsByShoppingList(account, 99L))
                .thenThrow(new ShoppingListNotFoundException());

        mockMvc.perform(get("/api/shopping-lists/{listId}/items", 99L).principal(authentication))
                .andExpect(status().isNotFound());
    }

    // ========== CREATE (ADD) ITEMS TESTS ==========

    @Test
    void addItem_returns201AndCreatedItem() throws Exception {
        // Prepare mock data
        AddItemRequestDTO request = new AddItemRequestDTO("100", 3);
        ShoppingListItemDTO dto = new ShoppingListItemDTO(5L, "100", 3);

        // Mock the service to return the created item
        when(shoppingListItemService.addItemToShoppingList(eq(account), eq(20L), any(AddItemRequestDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/shopping-lists/{listId}/items", 20L)
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productRef").value("100"))
                .andExpect(jsonPath("$.quantity").value(3));

        verify(shoppingListItemService).addItemToShoppingList(eq(account), eq(20L), any());
    }

    @Test
    void addItem_returns404_whenProductNotFound() throws Exception {
        AddItemRequestDTO request = new AddItemRequestDTO("999", 1);

        when(shoppingListItemService.addItemToShoppingList(eq(account), eq(20L), any(AddItemRequestDTO.class)))
                .thenThrow(new ItemNotFoundException());

        mockMvc.perform(post("/api/shopping-lists/{listId}/items", 20L)
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_returns404_whenListNotFound() throws Exception {
        AddItemRequestDTO request = new AddItemRequestDTO("100", 1);

        when(shoppingListItemService.addItemToShoppingList(eq(account), eq(99L), any(AddItemRequestDTO.class)))
                .thenThrow(new ShoppingListNotFoundException());

        mockMvc.perform(post("/api/shopping-lists/{listId}/items", 99L)
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ========== UPDATE ITEM TESTS ==========

    @Test
    void updateItemQuantity_returns200AndUpdatedItem() throws Exception {
        // Prepare mock data
        UpdateItemQuantityRequestDTO request = new UpdateItemQuantityRequestDTO(5);
        ShoppingListItemDTO dto = new ShoppingListItemDTO(1L, "Milk", 5);

        when(shoppingListItemService.updateItemQuantity(eq(account), eq(20L), eq(1L), any(UpdateItemQuantityRequestDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(patch("/api/shopping-lists/{listId}/items/{itemId}", 20L, 1L)
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productRef").value("Milk"))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void updateItemQuantity_returns404_whenItemNotFound() throws Exception {
        UpdateItemQuantityRequestDTO request = new UpdateItemQuantityRequestDTO(5);

        when(shoppingListItemService.updateItemQuantity(eq(account), eq(20L), eq(99L), any(UpdateItemQuantityRequestDTO.class)))
                .thenThrow(new ItemNotFoundException());

        mockMvc.perform(patch("/api/shopping-lists/{listId}/items/{itemId}", 20L, 99L)
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ========== DELETE ITEM TESTS ==========

    @Test
    void removeItem_returns204_whenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/shopping-lists/{listId}/items/{itemId}", 20L, 1L)
                        .principal(authentication))
                .andExpect(status().isNoContent());
        verify(shoppingListItemService).removeItemFromShoppingList(account, 20L, 1L);
    }

    @Test
    void removeItem_returns404_whenItemNotFound() throws Exception {
        doThrow(new ItemNotFoundException())
                .when(shoppingListItemService).removeItemFromShoppingList(account, 20L, 99L);

        mockMvc.perform(delete("/api/shopping-lists/{listId}/items/{itemId}", 20L, 99L)
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeItem_returns404_whenListNotFound() throws Exception {
        doThrow(new ShoppingListNotFoundException())
                .when(shoppingListItemService).removeItemFromShoppingList(account, 99L, 1L);

        mockMvc.perform(delete("/api/shopping-lists/{listId}/items/{itemId}", 99L, 1L)
                        .principal(authentication))
                .andExpect(status().isNotFound());
    }
}