#!/bin/bash
DEPLOY_DIR_PATH=$(pwd)
cd  user-service/scripts/
./deploy.sh
cd $DEPLOY_DIR_PATH;
cd  frontend-app/scripts/
./deploy.sh

