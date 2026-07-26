package com.BagnSave.backend.vendor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.BagnSave.backend.vendor.dto.VendorDTO;

@Service
public class VendorService {
    
    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository){
        this.vendorRepository = vendorRepository;
    }

    public Page<VendorDTO> getVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable)
                .map(this::toDTO);
    }

    private VendorDTO toDTO(Vendor vendor) {
        return new VendorDTO(
                vendor.getId(),
                vendor.getName()
        );
    }
}
