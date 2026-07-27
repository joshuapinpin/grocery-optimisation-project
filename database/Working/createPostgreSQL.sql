
-- Level 0:

ALTER TABLE public_vendors 
    ADD CONSTRAINT pk_public_vendors PRIMARY KEY (id);

ALTER TABLE public_products 
    ADD CONSTRAINT pk_public_products PRIMARY KEY (id);

ALTER TABLE public_collections 
    ADD CONSTRAINT pk_public_collections PRIMARY KEY (id);


CREATE TABLE ACCOUNT(
    id INT,
    account_name VARCHAR(255),
    hashed_password VARCHAR(255),
    password_salt VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

-- Level 1:
ALTER TABLE public_barcodes
    ADD CONSTRAINT pk_public_barcodes PRIMARY KEY (barcode,product_id),
    ADD CONSTRAINT fk_public_barcodes_to_public_products FOREIGN KEY (product_id) REFERENCES PUBLIC_PRODUCTS(id);


ALTER TABLE PUBLIC_STORES
    ADD CONSTRAINT pk_public_stores PRIMARY KEY (id),
    ADD CONSTRAINT fk_public_stores_to_public_vendors FOREIGN KEY (vendor_id) REFERENCES PUBLIC_VENDORS(id);


CREATE TABLE SHOPPING_LIST(
    list_name VARCHAR(255),
    account_id INT,
    CONSTRAINT fk_shopping_list_to_account FOREIGN KEY (account_id) REFERENCES ACCOUNT(id),
    CONSTRAINT pk_shopping_list PRIMARY KEY (account_id,list_name)
);


ALTER TABLE PUBLIC_COLLECTION_MEMBERS
    ADD CONSTRAINT pk_public_collection_member PRIMARY KEY (collection_id,product_id),
    ADD CONSTRAINT fk_public_collection_member_to_public_products FOREIGN KEY (product_id) REFERENCES PUBLIC_PRODUCTS(id),
    ADD CONSTRAINT fk_public_collection_member_to_public_collections FOREIGN KEY (collection_id) REFERENCES PUBLIC_COLLECTIONS(id);


ALTER TABLE PUBLIC_COLLECTION_HIERARCHY
    ADD CONSTRAINT pk_public_hierarchy PRIMARY KEY (parent_id,child_id),
    ADD CONSTRAINT fk_public_collection_hierarchy_parent_to_public_collections FOREIGN KEY (parent_id) REFERENCES PUBLIC_COLLECTIONS(id),
    ADD CONSTRAINT fk_public_collection_hierarchy_child_to_public_collections FOREIGN KEY (child_id) REFERENCES PUBLIC_COLLECTIONS(id);


-- Level 2:
ALTER TABLE PUBLIC_PRICES
    ADD CONSTRAINT pk_public_prices PRIMARY KEY (updated_at,store_id,product_id),
    ADD CONSTRAINT fk_public_prices_to_public_products FOREIGN KEY (store_id) REFERENCES PUBLIC_STORES(id),
    ADD CONSTRAINT fk_public_prices_to_public_collections FOREIGN KEY (product_id) REFERENCES PUBLIC_PRODUCTS(id);

CREATE TABLE PRODUCT_ON_LIST(
    shopping_list_name VARCHAR(255),
    account_id INT,
    product_id INT,
    CONSTRAINT fk_product_on_list_to_shopping_list FOREIGN KEY (shopping_list_name,account_id) REFERENCES SHOPPING_LIST(list_name,account_id),
    CONSTRAINT fk_product_on_list_to_public_products FOREIGN KEY (product_id) REFERENCES public_products(id),
    CONSTRAINT pk_producd_on_list PRIMARY KEY (shopping_list_name,account_id,product_id)
);

CREATE TABLE GENERIC_ON_LIST(
    id INT,
    shopping_list_name VARCHAR(255),
    account_id INT,
    collection_id INT,
    
    huristic VARCHAR(255),
    search_term VARCHAR(255),

    CONSTRAINT fk_generic_on_list_to_shopping_list FOREIGN KEY (shopping_list_name,account_id) REFERENCES SHOPPING_LIST(list_name,account_id),
    CONSTRAINT fk_generic_on_list_to_public_collections FOREIGN KEY (collection_id) REFERENCES public_collections(id),
    CONSTRAINT pk_generic_on_list PRIMARY KEY (id,shopping_list_name,account_id,collection_id)
);






