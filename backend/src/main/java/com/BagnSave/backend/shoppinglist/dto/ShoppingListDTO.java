package com.BagnSave.backend.shoppinglist.dto;

import com.BagnSave.backend.product.dto.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ShoppingListDTO {
    private Long id;
    private String name;
    private List<ProductDTO> products;
}