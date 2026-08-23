package com.BagnSave.backend.shoppinglist.item;


import com.BagnSave.backend.shoppinglist.list.ShoppingList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shopping_list_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingList shoppingList;

    // Reference into the product catalogue (DuckDB)
    @Column(nullable = false, length = 100)
    private String productRef;

    @Column(nullable = false)
    private int quantity = 1;
}
