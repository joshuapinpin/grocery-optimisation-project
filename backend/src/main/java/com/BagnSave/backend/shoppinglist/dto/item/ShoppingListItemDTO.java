package com.BagnSave.backend.shoppinglist.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShoppingListItemDTO {
    private Long id;
    private String productRef;
    private int quantity;
}
