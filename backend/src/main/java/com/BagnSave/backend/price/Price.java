package com.BagnSave.backend.price;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "public_prices")
public class Price {

    @EqualsAndHashCode.Include
    @ToString.Include
    @EmbeddedId
    private PriceId id;

    private Integer originalPriceCent;
    private Integer salePriceCent;
    private Integer clubPriceCent;
    private Integer onlinePriceCent;
    private Integer multibuyPriceCent;
    private Integer multibuyQuantity;
    private Integer clubMultibuyPriceCent;
    private Integer clubMultibuyQuantity;
    private OffsetDateTime updatedAt;

    
}
