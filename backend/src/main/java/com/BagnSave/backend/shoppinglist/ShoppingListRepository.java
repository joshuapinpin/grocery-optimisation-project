package com.BagnSave.backend.shoppinglist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findByAccountId(Long accountId);
    Optional<ShoppingList> findByIdAndAccountId(Long id, Long accountId);
    boolean existsByAccountIdAndName(Long accountId, String name);
}
