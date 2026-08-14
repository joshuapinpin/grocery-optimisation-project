package com.BagnSave.backend.shoppinglist.list.dto;

import com.BagnSave.backend.shoppinglist.item.dto.ShoppingListItemDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ShoppingListDTO {
    private Long id;
    private Long accountId;
    private String name;
    private List<ShoppingListItemDTO> products;
}