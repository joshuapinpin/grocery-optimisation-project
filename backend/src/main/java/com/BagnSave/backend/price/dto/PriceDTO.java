package com.BagnSave.backend.price.dto;

import java.time.OffsetDateTime;
import lombok.Value;

@Value
public class PriceDTO {
    Integer storeId;
    Integer productId;
    Integer originalPriceCent;
    Integer salePriceCent;
    Integer clubPriceCent;
    Integer onlinePriceCent;
    Integer multibuyPriceCent;
    Integer multibuyQuantity;
    Integer clubMultibuyPriceCent;
    Integer clubMultibuyQuantity;
    OffsetDateTime updatedAt;
}
