package com.BagnSave.backend.shoppinglist.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequestDTO {
    @NotBlank
    private String productRef;
    private int quantity;
}
