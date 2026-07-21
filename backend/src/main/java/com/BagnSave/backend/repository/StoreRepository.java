package com.BagnSave.backend.repository;

import com.BagnSave.backend.model.Store;
import java.util.List;

public interface StoreRepository {
    List<Store> findAll();
}
