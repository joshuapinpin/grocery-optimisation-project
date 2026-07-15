FROM postgres:18.4-alpine3.24
WORKDIR /app/

# Default entry point
COPY /createDB.sh /docker-entrypoint-initdb.d/createDB.sh


COPY /exportFromDuckDB.sql /app/exportFromDuckDB.sql
COPY /createPostgreSQL.sql /app/createPostgreSQL.sql


# Env varables for the postgres docker images to use 
# Will create user account automatically
ENV POSTGRES_USER bashServiceAccount
ENV POSTGRES_PASSWORD temp
# Will create db automatically
ENV POSTGRES_DB bagnsave_db_v2


# Env varables for my script to use 
# Used by psql command in createDB.sh by default
ENV PGUSER bashServiceAccount
ENV PGPASSWORD temp
ENV DBNAME bagnsave_db_v2

ENV duckdbGet https://assets-prod.grocer.nz/public/base_v3.duckdb.br 

RUN chmod +x /app/createDB.sh