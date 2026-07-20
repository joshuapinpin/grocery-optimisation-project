package com.BagnSave.backend.service;

import com.BagnSave.backend.dto.VendorDTO;
import com.BagnSave.backend.model.Vendor;
import com.BagnSave.backend.repository.VendorRepository;
import org.springframework.stereotype.Service;

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
