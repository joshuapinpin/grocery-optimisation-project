# Docker Guide


## Setup Docker Container

- Go to the docker website website and download docker
```
https://docs.docker.com/engine/install/ubuntu/
```
- Once Docker is installed you can just run the build script located in the working directory.

```
./build.sh
```

- Check that the docker container launched 
```
sudo docker ps
```
- It should look like this if it is running. If it is not running go to trouble shooting and check the log files to see what failed.
```
CONTAINER ID   IMAGE                COMMAND                  CREATED          STATUS          PORTS                                         NAMES
f2be6459a630   grocer-db-pipeline   "docker-entrypoint.s…"   11 seconds ago   Up 11 seconds   0.0.0.0:5432->5432/tcp, [::]:5432->5432/tcp   my-grocer-db
```

## View database with PG admin

- To look at the database withe GUI i used PG admin. Download page linked here
```
https://www.pgadmin.org/download/
```

- Once it is open click add new server with the following details

Name (this can be what ever you want but this is what i went with)
```
bagnsave
```
- Then go to connections tab at the top of the pop up
- hostname
```
localhost
```

- Port (5432 is the default if you changed the docker containers port you will need to change this to match)
```
5432
```

- Username
```
bashServiceAccount
```

- Password
```
temp
```


## Connect database to backend

- Add a file called 
```
application-local.properties
```
- At this file path
```
/BagnSave/backend/src/main/resources/application-local.properties
```
- With this content
```
spring.datasource.url=jdbc:postgresql://localhost:5432/bagnsave_db_v2
spring.datasource.username=bashServiceAccount
spring.datasource.password=temp

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```
- User name and password may change. The current content will work as they are the default credenals i have set at the moment.


## Trouble shooting
- Check the docker logs with 
```
docker logs -f my-grocer-db
```


#### Can not run ```build.sh```
- Check that "./build.sh" is executable
```
ls -l
```
- It should look like this with an x in the 3rd slot for executable
```
-rwxrw-r-- 1 jack jack  184 Jul 16 09:59 build.sh
```
- If it does not then you can make it executable with
```
chmod u+x build.sh
```

#### Docker is running
- 

#### Can not bind to port
- This means that another service is running on the port. This could be postgresql running on your system 

- Check with 
```
sudo systemctl status postgresql
```

- If it is running it will look like this 
```
● postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/usr/lib/systemd/system/postgresql.service; enabled; preset: enabled)
     Active: active (exited) since Thu 2026-07-23 09:48:34 NZST; 2h 21min ago
 Invocation: 47ea1876d77547309754b4159242f554
    Process: 2319 ExecStart=/bin/true (code=exited, status=0/SUCCESS)
   Main PID: 2319 (code=exited, status=0/SUCCESS)
   Mem peak: 1.7M
        CPU: 5ms

Jul 23 09:48:34 calcite systemd[1]: Starting postgresql.service - PostgreSQL RDBMS...
Jul 23 09:48:34 calcite systemd[1]: Finished postgresql.service - PostgreSQL RDBMS.
```

- To shut down the local postgresql service run. Note if postgresql starts on start up you will need to do this every time.
```
sudo systemctl stop postgresql

```

- Check to see if it is still running 
```
sudo systemctl status postgresql
```
- Should look like this if it is stopped
```
○ postgresql.service - PostgreSQL RDBMS
     Loaded: loaded (/usr/lib/systemd/system/postgresql.service; enabled; preset: enabled)
     Active: inactive (dead) since Thu 2026-07-23 12:12:37 NZST; 25s ago
   Duration: 2h 24min 3.332s
 Invocation: 47ea1876d77547309754b4159242f554
    Process: 2319 ExecStart=/bin/true (code=exited, status=0/SUCCESS)
   Main PID: 2319 (code=exited, status=0/SUCCESS)
   Mem peak: 1.7M
        CPU: 5ms

Jul 23 09:48:34 calcite systemd[1]: Starting postgresql.service - PostgreSQL RDBMS...
Jul 23 09:48:34 calcite systemd[1]: Finished postgresql.service - PostgreSQL RDBMS.
Jul 23 12:12:37 calcite systemd[1]: postgresql.service: Deactivated successfully.
Jul 23 12:12:37 calcite systemd[1]: Stopped postgresql.service - PostgreSQL RDBMS.

```