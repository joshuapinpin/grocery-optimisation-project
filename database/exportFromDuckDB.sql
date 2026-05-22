INSTALL postgres;
LOAD postgres;

ATTACH 'dbname=bagnsave_db_v1 user=postgres password=postgres host=localhost' AS pg (TYPE POSTGRES);

CREATE TABLE pg.public_barcodes AS SELECT * FROM public_barcodes;
CREATE TABLE pg.public_collection_hierarchy AS SELECT * FROM public_collection_hierarchy;
CREATE TABLE pg.public_collection_members AS SELECT * FROM public_collection_members;
CREATE TABLE pg.public_collections AS SELECT * FROM public_collections;
CREATE TABLE pg.public_meta AS SELECT * FROM public_meta;
CREATE TABLE pg.public_price_history AS SELECT * FROM public_price_history;
CREATE TABLE pg.public_prices AS SELECT * FROM public_prices;
CREATE TABLE pg.public_products AS SELECT * FROM public_products;
CREATE TABLE pg.public_stores AS SELECT * FROM public_stores;
CREATE TABLE pg.public_vendors AS SELECT * FROM public_vendors;