package com.BagnSave.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String email;
    private String name;
}
