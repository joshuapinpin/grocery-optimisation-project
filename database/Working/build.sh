sudo docker build --no-cache -t grocer-db-pipeline .

sudo docker stop my-grocer-db

sudo docker rm my-grocer-db

sudo docker run --name my-grocer-db -p 5432:5432 -d grocer-db-pipeline