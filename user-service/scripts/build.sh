#!/bin/bash

echo "======================================================================="
echo " Building User Service"
echo "======================================================================="
echo " "
cd db/
./deploy-postgres-service.sh
echo "======================================================================="
echo " User Service Builded"
echo "======================================================================="
echo " "