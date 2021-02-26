#!/bin/bash
BUILD_DIR_PATH=$(pwd)
cd hazelcast
./deploy-hazelcast-service.sh
cd $BUILD_DIR_PATH
cd redis 
./redis-deploy.sh
cd $BUILD_DIR_PATH
cd  user-service/scripts/
./build.sh
