# harvest-holidays

Simple server which fetches Finnish holidays from officeholidays.com so they could be HARVESTed

## Prerequisite

You need to have a connection to redis server to make the app work. If you don't have one, you can create redis docker container with following config (please change the password)

Create file `~/redis/docker-compose.dev.yml` and add this content:

```yml
services:
  redis:
    image: docker.io/bitnami/redis:7.4.1
    environment:
      - REDIS_PASSWORD=your-redis-password
    ports:
      - '6379:6379'
    volumes:
      - 'redis_data:/bitnami/redis/data'
    networks:
      - local-db-network

volumes:
  redis_data:
    driver: local

networks:
  local-db-network:
    external: true
```

You will also need to create a bridge network for the containers. Run `docker network create -d bridge local-db-network` in your terminal.

Then you can just run `docker-compose -f ~/redis/docker-compose.dev.yml up -d`.

Note, redis and holidays api containers must be in the same network to make it work without opening any ports.

## Local development

Create `profiles.clj` file to your project root and add your redis configuration there, e.g.

`:environment "dev"` starts the local dev server on port 3388 (can be changed with `:port`)

```
{:dev  {:env {:redis-host "127.0.0.1"
              :redis-port "6379"
              :redis-pass "your-password"
              :environment "dev"}}}
```

## Build docker image

Add `prod` profile to your profiles.clj file

```
{:dev  {:env {:redis-host "127.0.0.1"
              :redis-port "6379"
              :redis-pass "your-password"
              :environment "dev"}}
 :prod  {:env {:redis-host "redis"
               :redis-port "your-redis-port"
               :redis-pass "your-redis-password"
               :environment "production"}}}
```

Then build the image with command `docker build -t harvest-holidays .`

## Run docker image

You can run the image e.g. by calling `docker run -d -P --name holidays-api harvest-holidays`

If you have redis in another container, you should add this container and the redis container in the same network `docker run -d -P --name holidays-api --network local-db-network harvest-holidays`

When the redis server is in another container, the `redis-host` environment variable should be that container's name. Note that you do not have to expose the redis port when using redis in docker container. If the containers shares the same network, it should work.

## Supported search parameters

country `"fi" | "se"` (optional, empty means show all)

year `number` (optional, empty means show all)
