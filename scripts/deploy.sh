#!/bin/bash
aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 205930636356.dkr.ecr.ap-south-1.amazonaws.com
docker pull 976193231076.dkr.ecr.ap-south-1.amazonaws.com/ecr-mfeed-repo:latest
docker run -d -p 8080:8080 976193231076.dkr.ecr.ap-south-1.amazonaws.com/ecr-mfeed-repo:latest
