#!/bin/bash

USER="111capitalusa"
echo "user: ${USER}"
docker login cloud.canister.io:5000 -u ${USER}
docker push cloud.canister.io:5000/111capitalusa/blog-americanfunding
