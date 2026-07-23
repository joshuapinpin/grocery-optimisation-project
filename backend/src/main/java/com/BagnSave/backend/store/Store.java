package com.BagnSave.backend.store;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "public_stores")
public class Store {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @ToString.Include
    private String name;
    
    private Boolean isEnabled;
    private Integer vendorId;    
}
