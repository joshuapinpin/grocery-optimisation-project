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

#checks if a file has been downloaded already
safeWget() {
    wait=$1
    getCommand=$2   #get command
    outputFilePath=$3     #name to rename to
    #Check if the duckDB has alreay been downloaded
    echo "outputFilePath : $outputFilePath"
    if [ ! -f "$outputFilePath" ]; then
        #If the file does not exist then download it 
        wget -O $outputFilePath $getCommand

        echo "waiting $wait sec to not DOS server"
        sleep $wait
    fi
}



#--------------------- Download & import Parquet ---------------------
downloadParquet() {
    # Creates a directory for parquet files
    parquetFilesDirectoryName="parquetFiles"
    mkdir -p $parquetFilesDirectoryName

    # Creates a directory for each vendors parquet files
    vendorName=$1
    vedorParquetFilesDirectoryName="$parquetFilesDirectoryName/$vendorName"
    mkdir -p $vedorParquetFilesDirectoryName
    
    vendoresStoreIDsFilePath=$2

    count=0

    while IFS= read -r line; do
        
        if [[ "$line" == "id" ]]; then
            echo "Skiping id line"
        else
            echo "Current store id: $line"
            parquetFileName="$line"
            parquetFilePath="$vedorParquetFilesDirectoryName/$parquetFileName"

            safeWget 1 "https://assets-prod.grocer.nz/public/prices_per_store_v3/public_prices_$line.parquet" "$parquetFilePath"

            #imports paraqute into duck db
            duckdb $duckdbFileName -c "INSERT INTO public_prices SELECT * FROM read_parquet('$parquetFilePath');"


            # Increment the counter and check the limit
            ((count++))
            if (( count >= 5 )); then
                echo "Reached limit of 5 stores. Exiting loop."
                break
            fi
            
        fi

        
    done < "$vendoresStoreIDsFilePath"
}


echo "--------------------- Start : Download Grocer DuckDB ---------------------"

echo "DuckDB get command                           : $duckdbGet"

#Cuts it down to only after the last / so "base_v3.duckdb.br"
duckdbCompressedFileName="${duckdbGet##*/}"
#Cuts off the .br extension so "base_v3.duckdb.br"
duckdbFileName="${duckdbCompressedFileName%.br}"
echo "DuckDB file name (no .br)                    : $duckdbFileName"

#Check if the duckDB has alreay been downloaded
safeWget 0 "$duckdbGet" "$duckdbFileName"

echo "--------------------- Finish : Download Grocer DuckDB ---------------------"


echo "--------------------- Start : Extract store ID numbers ---------------------"

# storeDataDirName
# storeDataDirName/vendorInfoFileName

# Literal 
# storeData
# storeData/vendorInfo.txt

storeDataDirName=storeData
vendorInfoFilePath="$storeDataDirName/vendorInfo.txt"

mkdir -p $storeDataDirName
# creates a txt with the vendor info 
duckdb $duckdbFileName -c "COPY (SELECT * FROM public_vendors) TO '$vendorInfoFilePath';"

echo "--------------------- Finish : Extract store ID numbers ---------------------"


echo "--------------------- Start : Download and import parquet Files ---------------------"

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
        downloadParquet "$vendorName" "$vendorStoreIDsFilePath"

    fi
    
done < "$vendorInfoFilePath"
echo "--------------------- Finish : Download and import parquet Files ---------------------"

echo "--------------------- Start : Migrate DuckDB to PostgreSQL  ---------------------"


# DBNAME is defined in dockerfile as an env varable
# uses PGUSER and PGPASSWORD env varables
createdb $DBNAME

#connects to then exports the duck db tables to the postgreSQL database
duckdb "$duckdbFileName" -c ".read exportFromDuckDB.sql"


echo "--------------------- Add user account and shopping list relations  ---------------------"
psql -d $DBNAME -f "createPostgreSQL.sql"


echo "--------------------- Finish : Migrate DuckDB to PostgreSQL  ---------------------"
