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

