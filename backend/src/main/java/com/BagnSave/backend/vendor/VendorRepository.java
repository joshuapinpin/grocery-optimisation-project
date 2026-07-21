package com.BagnSave.backend.vendor;

import java.util.List;
import java.util.Optional;

public interface VendorRepository {
    List<Vendor> findAll();
    Optional<Vendor> findById(int id);
}
