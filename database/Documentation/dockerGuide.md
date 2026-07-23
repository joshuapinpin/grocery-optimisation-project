# Docker Guide


## Download Docker

- Go to the docker website website
```
https://docs.docker.com/engine/install/ubuntu/
```


- Once Docker is installed you can just run the build script

```
./build.sh
```

- To look at the 

```
https://www.pgadmin.org/download/
```

## Trouble shooting

#### /build.sh is execuable
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