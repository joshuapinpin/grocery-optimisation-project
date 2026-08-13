package com.BagnSave.backend.shoppinglist.dto.list;

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