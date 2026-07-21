package com.BagnSave.backend.repository;

import com.BagnSave.backend.model.Vendor;
import java.util.List;
import java.util.Optional;

public interface VendorRepository {
    List<Vendor> findAll();
    Optional<Vendor> findById(int id);
}
