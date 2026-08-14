package com.BagnSave.backend.shoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.auth.AuthenticatedAccountResolver;
import com.BagnSave.backend.shoppinglist.exception.ShoppingListNotFoundException;
import com.BagnSave.backend.shoppinglist.list.ShoppingListController;
import com.BagnSave.backend.shoppinglist.list.ShoppingListService;
import com.BagnSave.backend.shoppinglist.list.dto.CreateListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.RenameListRequestDTO;
import com.BagnSave.backend.shoppinglist.list.dto.ShoppingListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class ShoppingListControllerTest {

    private ShoppingListService shoppingListService;
    private AuthenticatedAccountResolver accountResolver;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Account account;
    private Authentication authentication;

    @BeforeEach
    void setup() {
        shoppingListService = mock(ShoppingListService.class);
        accountResolver = mock(AuthenticatedAccountResolver.class);
        ShoppingListController controller =
                new ShoppingListController(shoppingListService, accountResolver);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        account = new Account();
        account.setId(1L);
        authentication = new UsernamePasswordAuthenticationToken("user@example.com", "pw");
        when(accountResolver.resolve(any(Authentication.class))).thenReturn(account);
    }

    @Test
    void getAllShoppingLists_returns200AndBody() throws Exception {
        ShoppingListDTO dto = new ShoppingListDTO(1L, account.getId(), "Weekly", List.of());
        when(shoppingListService.getListsForAccount(account)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/shopping-lists").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Weekly"));
    }

    @Test
    void getShoppingList_returns200AndBody_whenExists() throws Exception {
        ShoppingListDTO dto = new ShoppingListDTO(5L, account.getId(), "Party", List.of());
        when(shoppingListService.getList(account, 5L)).thenReturn(dto);

        mockMvc.perform(get("/api/shopping-lists/5").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Party"));
    }

    @Test
    void getShoppingList_returns404_whenServiceThrowsNotFound() throws Exception {
        when(shoppingListService.getList(account, 999L))
                .thenThrow(new ShoppingListNotFoundException());

        mockMvc.perform(get("/api/shopping-lists/999").principal(authentication))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShoppingList_returns201_whenValid() throws Exception {
        CreateListRequestDTO request = new CreateListRequestDTO("Weekly");
        ShoppingListDTO dto = new ShoppingListDTO(10L, account.getId(), "Weekly", List.of());
        when(shoppingListService.createList(eq(account), any(CreateListRequestDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/shopping-lists")
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Weekly"));
    }

    @Test
    void renameShoppingList_returns200_whenValid() throws Exception {
        RenameListRequestDTO request = new RenameListRequestDTO("New Name");
        ShoppingListDTO dto = new ShoppingListDTO(5L, account.getId(), "New Name", List.of());
        when(shoppingListService.renameList(eq(account), eq(5L), any(RenameListRequestDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/shopping-lists/5")
                        .principal(authentication)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void deleteShoppingList_returns204_whenValid() throws Exception {
        mockMvc.perform(delete("/api/shopping-lists/5").principal(authentication))
                .andExpect(status().isNoContent());

        verify(shoppingListService).deleteList(account, 5L);
    }
}