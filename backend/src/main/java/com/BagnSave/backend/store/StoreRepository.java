package com.BagnSave.backend.store;

import java.util.List;

public interface StoreRepository {
    List<Store> findAll();
}
