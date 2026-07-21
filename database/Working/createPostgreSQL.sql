ALTER TABLE public_collections 
    ADD CONSTRAINT pk_public_collections PRIMARY KEY (id);

ALTER TABLE public_products 
    ADD CONSTRAINT pk_public_products PRIMARY KEY (id);




CREATE TABLE ACCOUNT(
    id INT,
    account_name VARCHAR(255),
    hashed_password VARCHAR(255),
    password_salt VARCHAR(255),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE TABLE SHOPPING_LIST(
    account_id INT,
    list_name VARCHAR(255),
    CONSTRAINT fk_shopping_list_to_account FOREIGN KEY (account_id) REFERENCES ACCOUNT(id),
    CONSTRAINT pk_shopping_list PRIMARY KEY (account_id,list_name)
);

CREATE TABLE PRODUCT_ON_LIST(
    public_products_id INT,
    account_id INT,
    list_name VARCHAR(255),
    CONSTRAINT fk_product_on_list_to_shopping_list FOREIGN KEY (account_id,list_name) REFERENCES SHOPPING_LIST(account_id,list_name),
    CONSTRAINT fk_product_on_list_to_public_products FOREIGN KEY (public_products_id) REFERENCES public_products(id),
    CONSTRAINT pk_producd_on_list PRIMARY KEY (public_products_id,account_id,list_name)
);

CREATE TABLE GENERIC_ON_LIST(
    generid_id INT,
    public_collections_id INT,
    account_id INT,
    list_name VARCHAR(255),
    
    huristic VARCHAR(255),
    search_term VARCHAR(255),

    CONSTRAINT fk_generic_on_list_to_shopping_list FOREIGN KEY (account_id,list_name) REFERENCES SHOPPING_LIST(account_id,list_name),
    CONSTRAINT fk_generic_on_list_to_public_collections FOREIGN KEY (public_collections_id) REFERENCES public_collections(id),
    CONSTRAINT pk_generic_on_list PRIMARY KEY (generid_id,public_collections_id,account_id,list_name)
);






