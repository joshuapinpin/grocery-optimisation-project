
# Automatic Guide

This guide is intended to show you how to use the automatic script. For info on the process please see the manual guide.


## Table of Contents
- Install DuckDB CLI
- Install postgreSQL
- Clean up postgreSQL
- Execute Automatic Script

---
## Install DuckDB CLI 

- Instructions are on the DuckDB website
```
https://duckdb.org/install/?platform=linux&environment=cli
```

- Command i used
```
curl https://install.duckdb.org | sh
```

- I had to add it to my bash profile 

```
nano ~/.bashrc
```
- (i added this line as directed)
```
export PATH="/home/jack/.duckdb/cli/latest":$PATH
```

---
## Install postgreSQL

- Download 
```
sudo apt install postgresql postgresql-contrib

```

- Check service status
```
systemctl status postgresql
```

- restart service 
```
systemctl restart postgresql
```

- Open PostgreSQL CLI
```
sudo -u postgres psql
```

- Set password for postgres user
- ***Please Note** This password is hard coded into the script for simplicity so please do not change it*
```
ALTER USER postgres PASSWORD 'postgres';
```


---
## CLean up postgreSQL
- Some times the script fails and you may need to delete the postgreSQL database. This can be done by. 
- List all the databases to see if one is there we are looking for *"bagnsave_db_v1"*
```
\l
```
- To exit this list
```
q
```

- Delete the database
```
DROP DATABASE bagnsave_db_v1;
```


---
## Execute Automatic Script
- Add execute permissions to the bash script
```
chmod u+x createDB.sh
```

- Execute the bash script
- *See manual guide for how to find the GET command.*
```
./createDB.sh https://assets-prod.grocer.nz/public/base_v3.duckdb.br
```

---
## Access Database
- Open PostgreSQL CLI
```
sudo -u postgres psql
```

- Connect to the database
```
\c bagnsave_db_v1
```

- List Tables
```
\dt
```