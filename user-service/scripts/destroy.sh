#!/bin/bash
echo "======================================================================="
echo " Destroying User Service"
echo "======================================================================="
echo " "
cd db/
sudo ./drop-postgres-service.sh
echo " "
echo "======================================================================="
echo " User Service Destroyed"
echo "======================================================================="
echo " "