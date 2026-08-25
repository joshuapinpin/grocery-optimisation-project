package com.BagnSave.backend.barcode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.BagnSave.backend.barcode.dto.BarcodeDTO;

@Service
public class BarcodeService {
    
    private final BarcodeRepository barcodeRepository;

    public BarcodeService(BarcodeRepository barcodeRepository) {
        this.barcodeRepository = barcodeRepository;
    }

    public Page<BarcodeDTO> getBarcodes(Pageable pageable) {
        return barcodeRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private BarcodeDTO toDTO(Barcode barcode) {
        return new BarcodeDTO(
            barcode.getId().getBarcode(),
            barcode.getId().getProductId()
        );
    }

}
