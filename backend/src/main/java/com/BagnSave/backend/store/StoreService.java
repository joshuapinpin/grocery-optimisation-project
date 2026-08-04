package com.BagnSave.backend.store;

import com.BagnSave.backend.store.dto.StoreDTO;
import com.BagnSave.backend.vendor.Vendor;
import com.BagnSave.backend.vendor.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final VendorRepository vendorRepository;

    public StoreService(StoreRepository storeRepository, VendorRepository vendorRepository) {
        this.storeRepository = storeRepository;
        this.vendorRepository = vendorRepository;
    }
    
    public Page<StoreDTO> getStores(Pageable pageable) {
        return storeRepository.findAll(pageable)
                .map(this::toDTO);
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

    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
}
