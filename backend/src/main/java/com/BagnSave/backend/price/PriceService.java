package com.BagnSave.backend.price;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.BagnSave.backend.price.dto.PriceDTO;

@Service
public class PriceService {
    
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Page<PriceDTO> getPrices(Pageable pageable) {
        return priceRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private PriceDTO toDTO(Price price) {
        return new PriceDTO(
            price.getId().getStoreId(), 
            price.getId().getProductId(),
            price.getOriginalPriceCent(),
            price.getSalePriceCent(),
            price.getClubPriceCent(),
            price.getOnlinePriceCent(),
            price.getMultibuyPriceCent(),
            price.getMultibuyQuantity(),
            price.getClubMultibuyPriceCent(),
            price.getClubMultibuyQuantity(),
            price.getUpdatedAt()
        );
    }
}
