# Database Readme


---
## Table of Contents
- Download DuckDB
- Using duckdb
- Downloading parquet files
- Merging & migrating to postgres SQL
- Layout and Structure


---
## Download DuckDB

- Load Grocer webstite with dev tools enabled

```
https://grocer.nz/
```

- Search network tab for duck and look for the database file

- Find the GET request and extract the URL
```
https://assets-prod.grocer.nz/public/base_v3.duckdb.br
```

- Use URL to download the DuckDB
```
wget https://assets-prod.grocer.nz/public/base_v3.duckdb.br
```

- Rename to remove .br extension
```
mv base_v3.duckdb.br base_v3.duckdb
```

---
## Using duckdb

- start up duckDB CLI (command line interface)

```
duckdb base_v3.duckdb 
```

- I change the output Format as i dislike the boxes that duckbd uses by default
```
.mode markdown
```

- The CLI takes standard SQL Queries

- List tables with this command 
```
SHOW TABLES;
```
|            name             |
|-----------------------------|
| public_barcodes             |
| public_collection_hierarchy |
| public_collection_members   |
| public_collections          |
| public_meta                 |
| public_price_history        |
| public_prices               |
| public_products             |
| public_stores               |
| public_vendors              |
base_v3 D 

- List the vendors and their ID numbers
```
SELECT * FROM public_vendors;
```
| id |     name      |
|---:|---------------|
| 1  | Woolworths    |
| 2  | New World     |
| 3  | Pak'nSave     |
| 4  | The Warehouse |
| 5  | Fresh Choice  |
| 6  | Super Value   |



- The "public_stores" table stores the infomation about each physical store. The "vendor_id" attribute is the same as in the above table. Notice all the "vendor_id" values are 1 as they are all woolworth stores.

- The ID number of each store is very useful. This is becasue the item information for each store is stored in a seperate parquet file. Not in the DuckDB.

```
SELECT * FROM public_stores LIMIT 5;
```
| id | vendor_id |            name             | is_enabled |
|---:|----------:|-----------------------------|------------|
| 7  | 1         | Woolworths Ashburton South  | true       |
| 8  | 1         | Woolworths Auckland Airport | true       |
| 46 | 1         | Woolworths Gore             | true       |
| 53 | 1         | Woolworths Hastings Central | true       |
| 54 | 1         | Woolworths Hauraki Corner   | true       |


- I have exported the "id" numbers  of all the stores and split them up based on vendor with the following commands. These id numbers will be used to download all the parquet files.parquet


- Notice "vendor_id=1" in the SELECT command when saving the wool worth id numbers as 1 is the vendor id of woolworths in the vendors table above.
```
COPY (SELECT id FROM public_stores WHERE vendor_id=1) TO 'woolWorthID.txt';
```

```
COPY (SELECT id FROM public_stores WHERE vendor_id=2) TO 'newWorldID.txt';
```

```
COPY (SELECT id FROM public_stores WHERE vendor_id=3) TO 'pakNSave.txt';
```


---
## Downloading parquet files

- To download 1 stores item infomation use the following command

```
wget https://assets-prod.grocer.nz/public/prices_per_store_v3/public_prices_7.parquet
```

- To auomate downloading a list of stores i have written a shell script called "downloadParquetFiles.sh"

```
#!/bin/bash

#First position is the file of id numbers
filename=$1

echo "Loaded file : $filename"
echo "Staring loop"
while IFS= read -r line; do
    
    if [[ "$line" == "id" ]]; then
        echo "Skiping id line"
    else
        echo "Current store id: $line"
        wget https://assets-prod.grocer.nz/public/prices_per_store_v3/public_prices_$line.parquet 
        echo "waiting 5 sec to not DOS server"
        sleep 5
    fi
    
done < "$filename"
```

- To run the shell script it must be given the execute permission

```
chmod u+x downloadParquetFiles.sh
```

- The commands to run the shell script are as follows

```
./downloadParquetFiles.sh <path to file of store id numbers>
```

```
./downloadParquetFiles.sh storeIDNumbers/woolWorthID.txt
```

```
./downloadParquetFiles.sh storeIDNumbers/newWorldID.txt
```

```
./downloadParquetFiles.sh storeIDNumbers/pakNSave.txt
```




---
## Using postgreSQL

launch postgreSQL
```
sudo -u postgres psql
```



Create DB
```
CREATE DATABASE bagnsave_db_v1;
```

Connect to DB
```
\c bagnsave_db_v1
```


duckdb base_v3.duckdb


INSTALL postgres;
LOAD postgres;

ATTACH 'dbname=bagnsave_db_v1 user=postgres password=postgres host=localhost' AS pg (TYPE POSTGRES);



CREATE TABLE pg.public_vendors AS SELECT * FROM public_vendors;



DETACH pg;




### CLean up postgreSQL

List all the databases 
```
\l
```
to exit this list
```
q
```

Delete the database
```
DROP DATABASE bagnsave_db_v1;
```




