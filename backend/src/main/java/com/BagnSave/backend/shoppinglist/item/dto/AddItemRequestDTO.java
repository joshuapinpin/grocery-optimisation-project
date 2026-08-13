package com.BagnSave.backend.shoppinglist.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddItemRequestDTO {
    private String productRef;
    private int quantity;
}
