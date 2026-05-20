#!/bin/bash

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


#--------------------- Download Parquet ---------------------

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

