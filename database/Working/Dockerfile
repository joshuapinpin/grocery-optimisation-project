FROM postgres:18.4-alpine3.24
WORKDIR /app/
COPY /createDB.sh /app/createDB.sh
COPY /exportFromDuckDB.sql /app/exportFromDuckDB.sql
COPY /createPostgreSQL.sql /app/createPostgreSQL.sql

ENV PGUSER bashServiceAccount
ENV PGPASSWORD temp
ENV dbname bagnsave_db_v2