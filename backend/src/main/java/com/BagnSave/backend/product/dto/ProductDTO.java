package com.BagnSave.backend.product.dto;

import lombok.Value;

@Value
public class ProductDTO {
    Integer id;
    String name;
    String brand;
    String unit;
    String size;
    Integer redirectedTo;
}
