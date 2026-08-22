package com.BagnSave.backend.price;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PriceId implements Serializable {

    private OffsetDateTime updatedAt;
    private Integer storeId;
    private Integer productId;
    
}
