#!/bin/bash


# File structure 

# |--| storeData
# |  |----> vendorInfo.txt
# |  |----> newWorld.txt
# |  |----> woolWorths.txt
# |
# |--| parquetFiles
#    |--| newWorld
#    |  |--> public_prices_7.parquet
#    |  |--> public_prices_8.parquet
#    |  |--> public_prices_9.parquet
#    |
#    |--| woolWorths
#       |--> public_prices_10.parquet
#       |--> public_prices_11.parquet
#       |--> public_prices_12.parquet


#--------------------- Download Parquet ---------------------
downloadParquet() {
    # Creates a directory for parquet files
    parquetFilesDirectoryName=parquetFiles
    mkdir -p $parquetFilesDirectoryName

    # Creates a directory for each vendors parquet files
    vendorName=$1
    vedorParquetFilesDirectoryName="$parquetFilesDirectoryName/$vendorName"
    mkdir -p $vedorParquetFilesDirectoryName
    
    vendoresStoreIDsFilePath=$2

    while IFS= read -r line; do
        
        if [[ "$line" == "id" ]]; then
            echo "Skiping id line"
        else
            echo "Current store id: $line"
            wget -P $vedorParquetFilesDirectoryName https://assets-prod.grocer.nz/public/prices_per_store_v3/public_prices_$line.parquet 
            echo "waiting 5 sec to not DOS server"
            sleep 5
        fi
        
    done < "$vendoresStoreIDsFilePath"
}




#--------------------- Download Duck DB ---------------------
#First argument is duckdbget command 
#should look like : https://assets-prod.grocer.nz/public/base_v3.duckdb.br
duckdbGet=$1
echo "DuckDB get command                           : $duckdbGet"

# last 17 char are the duckdb filename with the .br extension
duckdbCompressedFileName="${duckdbGet##*/}"
echo "Compressed DuckDB file name                  : $duckdbCompressedFileName"

# first 15 char are jsut the duckdb filename
duckdbFileName="${duckdbCompressedFileName%.br}"
echo "DuckDB file name (no .br)                    : $duckdbFileName"

#Check if the duckDB has alreay been downloaded
if [ ! -f "$duckdbFileName" ]; then
    echo "DuckDB does not exist already, Downloading ..."
    #If the file does not exist then download it 
    #Actual download command
    wget $duckdbGet

    #Rename downloaded duckdb to remoe .br extension
    mv $duckdbCompressedFileName $duckdbFileName
fi

#--------------------- Extract Vendor IDs ---------------------

# storeDataDirName
# storeDataDirName/vendorInfoFileName

# Literal 
# storeData
# storeData/vendorInfo.txt

storeDataDirName=storeData
vendorInfoFilePath="$storeDataDirName/vendorInfo.txt"

mkdir -p $storeDataDirName
# creates a txt with the vendor info 
duckdb base_v3.duckdb -c "COPY (SELECT * FROM public_vendors) TO '$vendorInfoFilePath';"

while IFS= read -r line; do
    if [[ "$line" == "id,name" ]]; then
        echo "Skiping first line"
    else
        #The id is before the comma
        vendorID="${line%,*}"
        #The name is after the comma
        rawVendorName="${line#*,}"
        #Removes all special charicters
        vendorName="${rawVendorName//[^a-zA-Z]/}"
        
        echo "Current Vendor : $vendorName with ID : $vendorID"

        vendorStoreIDsFilePath="$storeDataDirName/$vendorName.txt"
        #Extract store ids
        duckdb base_v3.duckdb -c "COPY (SELECT id FROM public_stores WHERE vendor_id=$vendorID) TO '$vendorStoreIDsFilePath';"

        #download all the Parquet files for each vendor using the store ids 
        downloadParquet "$vendorName" "$vendoresStoreIDsFilePath"

    fi
    
done < "$vendorInfoFilePath"





