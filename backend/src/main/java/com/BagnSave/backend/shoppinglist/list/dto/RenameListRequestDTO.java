package com.BagnSave.backend.shoppinglist.list.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RenameListRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String name;
}
