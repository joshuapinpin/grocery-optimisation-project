package com.BagnSave.backend.barcode;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.BagnSave.backend.barcode.dto.BarcodeDTO;
import com.BagnSave.backend.shared.PaginationDefaults;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeController {
    
    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @GetMapping
    public Page<BarcodeDTO> getBarcodes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "" + PaginationDefaults.DEFAULT_PAGE_SIZE) int size
    ) {
        int safeSize = Math.min(size, PaginationDefaults.MAX_PAGE_SIZE);
        return barcodeService.getBarcodes(PageRequest.of(page, safeSize));
    }
}
