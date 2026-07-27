
## Extended Entity Relation Diagram (EER diagram)
![Alt text](whole_DB_EER_v2.png)

## Entity Relation Schema (ER schema)

#### Level 0:
PUBLIC_VENDORS = ({id,name},{id})

PUBLIC_PRODUCTS = ({id, name, brand, unit, size},{id})

PUBLIC_COLLECTIONS = ({id, name, is_comparable},{id})

ACCOUNT = ({id, user_name,hashed_password,password_salt},{id})
K
#### Level 1:
PUBLIC_BARCODES =  ({barccode, PUBLIC_PRODUCTS},{barccode, PUBLIC_PRODUCTS})

PUBLIC_STORES =  ({id, PUBLIC_VENDORS,is_enabled,name},{id, PUBLIC_VENDORS})

PUBLIC_SHOOPING_LIST =  ({list_name, ACCOUNT},{list_name, ACCOUNT})

PUBLIC_COLLECTION_MEMBERS =  ({PUBLIC_PRODUCTS, PUBLIC_COLLECTIONS},{PUBLIC_PRODUCTS, PUBLIC_COLLECTIONS})

PUBLIC_COLLECTION_HIERARCHY =  ({PARENT_PUBLIC_COLLECTIONS, CHILD_PUBLIC_COLLECTIONS},{PARENT_PUBLIC_COLLECTIONS, CHILD_PUBLIC_COLLECTIONS})

#### level 2:
PUBLIC_PRICES = ({updated_at,PUBLIC_STORES,PUBLIC_PRODUCTS,original_price_cent,sale_price_cent,club_price_cent,multibuy_price_cent, multibuy_quantity,club_multibuy_price_cent, club_multibuy_quantity, online_price_cent},{updated_at,PUBLIC_STORES,PUBLIC_PRODUCTS})

PRODUCT_ON_LIST = ({SHOPPING_LIST,PUBLIC_PRODUCTS},{SHOPPING_LIST,PUBLIC_PRODUCTS})

GENERIC_ON_LIST = ({generic_id,SHOPPING_LIST,huristic,search_term},{generic_id,SHOPPING_LIST})


## Relational Database Schema


