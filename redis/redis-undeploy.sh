#!/bin/bash 
echo "UnDeploying " 
microk8s.kubectl delete deployment  user-service-redis-db 
microk8s.kubectl delete service  user-service-redis-db
