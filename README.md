# harvest-holidays

Simple server which fetches Finnish holidays from officeholidays.com so they could be HARVESTed

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

If you have redis in another container, you should add this container and the redis container in the same network `docker run -d -P --name holidays-api --network yournetwork harvest-holidays`

When the redis server is in another container, the `redis-host` environment variable should be that container's name. Note that you do not have to expose the redis port when using redis in docker container. If the containers shares the same network, it should work.

## Supported search parameters

country `"fi" | "se"` (optional, empty means show all)

year `number` (optional, empty means show all)
