package com.BagnSave.backend.service;

import com.BagnSave.backend.dto.StoreDTO;
import com.BagnSave.backend.model.Store;
import com.BagnSave.backend.model.Vendor;
import com.BagnSave.backend.repository.StoreRepository;
import com.BagnSave.backend.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupermarketService {

    private final StoreRepository storeRepository;
    private final VendorRepository vendorRepository;

    public SupermarketService(StoreRepository storeRepository, VendorRepository vendorRepository) {
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
                store.IsEnabled(),
                vendorName
        );
    }
}
