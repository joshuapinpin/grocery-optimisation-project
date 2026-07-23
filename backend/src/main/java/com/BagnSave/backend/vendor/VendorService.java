package com.BagnSave.backend.vendor;

import org.springframework.stereotype.Service;

import com.BagnSave.backend.vendor.dto.VendorDTO;

import java.util.List;

@Service
public class VendorService {
    
    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository){
        this.vendorRepository = vendorRepository;
    }

    public List<VendorDTO> getAllVendors() {
        List<Vendor> vendors = vendorRepository.findAll();

        return vendors.stream()
                .map(this::toDTO)
                .toList();
    }

    private VendorDTO toDTO(Vendor vendor) {
        return new VendorDTO(
                vendor.getId(),
                vendor.getName()
        );
    }
}
