# harvest-holidays

Simple server which fetches Finnish holidays from officeholidays.com so they could be HARVESTed

## Build docker image

Create `.lein-env` file to your project root and add your redis configuration there, e.g.

```
{:redis-host "redishost"
 :redis-port "redisport"
 :redis-pass "redispassword"}
```

Then build the image with command `docker build -t harvest-holidays .`

## Run docker image

You can run the image e.g. by calling `docker run -d -P --name holidays-api harvest-holidays`

If you have redis in another container, you should add this container and the redis container in the same network `docker run -d -P --name holidays-api --network yournetwork harvest-holidays`

When the redis server is in another container, the `redis-host` should be that container's name. Note that you do not have to expose the redis port when using redis in docker container. If the containers shares the same network, it should work.

## Supported search parameters

country `"fi" | "se"` (optional, empty means show all)

year `number` (optional, empty means show all)
