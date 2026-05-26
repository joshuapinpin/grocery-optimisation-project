package com.BagnSave.backend.mockshoppinglist.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateListRequest(
    @NotBlank String name
) {}
