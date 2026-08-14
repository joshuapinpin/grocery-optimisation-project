package com.BagnSave.backend.shoppinglist.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemQuantityRequestDTO {
    private int quantity;
}
