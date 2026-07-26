package com.BagnSave.backend.store.dto;

import lombok.Value;

@Value
public class StoreDTO {
    Integer id;
    String name;
    Boolean isEnabled;
    String vendorName;
}
