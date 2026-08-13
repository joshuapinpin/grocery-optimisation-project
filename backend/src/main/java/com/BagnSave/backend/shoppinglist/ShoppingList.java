package com.BagnSave.backend.shoppinglist;

import com.BagnSave.backend.auth.Account;
import com.BagnSave.backend.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "shopping_lists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void touch(){
        updatedAt = Instant.now();
    }

}
