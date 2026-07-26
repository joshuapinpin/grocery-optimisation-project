package com.BagnSave.backend.store;

import com.BagnSave.backend.store.dto.StoreDTO;
import com.BagnSave.backend.vendor.Vendor;
import com.BagnSave.backend.vendor.VendorRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final VendorRepository vendorRepository;

    public StoreService(StoreRepository storeRepository, VendorRepository vendorRepository) {
        this.storeRepository = storeRepository;
        this.vendorRepository = vendorRepository;
    }
    
    public List<StoreDTO> getAllStores() {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(this::toDTO)
                .toList();
    }

    private StoreDTO toDTO(Store store) {
        Optional<Vendor> vendor = vendorRepository.findById(store.getVendorId());
        String vendorName = vendor.map(Vendor::getName).orElse("Unknown Vendor");

        return new StoreDTO(
                store.getId(),
                store.getName(),
                store.getIsEnabled(),
                vendorName
        );
    }
}
