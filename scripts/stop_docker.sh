#!/bin/bash
docker-compose stop app_mfeed
docker-compose rm app_mfeed -f
docker-compose stop redis
docker-compose rm redis -f

# Optionally, remove unused volumes and networks
docker volume prune -f
docker network prune -f

