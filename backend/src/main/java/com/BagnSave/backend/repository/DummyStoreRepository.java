package com.BagnSave.backend.repository;

import com.BagnSave.backend.model.Store;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class DummyStoreRepository implements StoreRepository {
    
    public final List<Store> stores = List.of(
        new Store(1, "PakNSave Kilbernie", true, 1),
        new Store(2, "Woolworths Kilbernie", true, 2),
        new Store(3, "New World Miramar", false, 3),
        new Store(4, "PaknSave Petone", true, 1)
    );

    @Override
    public List<Store> findAll() {
        return stores;
    }
}
