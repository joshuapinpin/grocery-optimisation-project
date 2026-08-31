FROM postgres:18.4
WORKDIR /app/

# Install duckdb CLI
# standard tools
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Run the installer, then move the binary into a standard system PATH
RUN curl https://install.duckdb.org | bash && \
    mv /root/.duckdb/cli/latest/duckdb /usr/local/bin/duckdb

# Copys create script into entry point to run after postgresql starts
COPY /createDB.sh /docker-entrypoint-initdb.d/createDB.sh

COPY /exportFromDuckDB.sql /app/exportFromDuckDB.sql
COPY /createPostgreSQL.sql /app/createPostgreSQL.sql

RUN chown -R postgres:postgres /app /docker-entrypoint-initdb.d/createDB.sh

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

RUN chmod +x /docker-entrypoint-initdb.d/createDB.sh