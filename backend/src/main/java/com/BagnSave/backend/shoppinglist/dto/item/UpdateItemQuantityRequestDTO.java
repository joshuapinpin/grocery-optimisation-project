package com.BagnSave.backend.shoppinglist.dto.item;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateItemQuantityRequestDTO {
    private int quantity;
}
