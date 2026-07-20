package com.BagnSave.backend.repository;

import com.BagnSave.backend.model.Vendor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class DummyVendorRepository implements VendorRepository{
    
    private final List<Vendor> vendors = List.of(
        new Vendor(1, "PakNSave"),
        new Vendor(2, "Woolworths"),
        new Vendor(3, "New World")
    );
    
    @Override
    public List<Vendor> findAll() {
        return vendors;
    }

    @Override
    public Optional<Vendor> findById(int id){
        return vendors.stream()
                    .filter(v -> v.getId() == id)
                    .findFirst();
    }
}
