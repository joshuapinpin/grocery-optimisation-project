package com.BagnSave.backend.price;

import java.util.List;

import org.springframework.stereotype.Service;

import com.BagnSave.backend.price.dto.PriceDTO;

@Service
public class PriceService {
    
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<PriceDTO> getAllPrices() {
        List<Price> prices = priceRepository.findAll();

        return prices.stream()
                .map(this::toDTO)
                .toList();
    }

    private PriceDTO toDTO(Price price) {
        return new PriceDTO(
            price.getId().getStoreId(), 
            price.getId().getProductId(),
            price.getOriginalPriceCent(),
            price.getSalePriceCent(),
            price.getClubPriceCent(),
            price.getOnlinePriceCent(),
            price.getMultibuyPriceInteger(),
            price.getMultibuyQuantity(),
            price.getClubMultibuyPriceCent(),
            price.getClubMultibuyQuantity(),
            price.getUpdatedAt()
        );
    }
}
