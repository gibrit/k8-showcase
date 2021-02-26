#!/bin/bash
echo "======================================================================="
echo " Deploying Frontend App Service"
echo "======================================================================="
echo " "
DIR_PATH=$(pwd)
cd .. 
mvn clean 
mvn dependency:copy-dependencies compile 
docker build -t saltuk-kubernetes/frontend-app-service .&
wait %1
docker images &
wait %1
docker save saltuk-kubernetes/frontend-app-service:latest > saltuk-kubernetes.frontend-app-service.tar &
wait %1
microk8s.ctr image import saltuk-kubernetes.frontend-app-service.tar &
wait %1
echo "================================================="
echo "Docker Build Success"
echo "================================================="
cd $DIR_PATH  ;

microk8s.kubectl delete -f config/ingress.yaml &
wait %1;
microk8s.kubectl delete -f config/service.yaml &
wait %1;
microk8s.kubectl delete -f config/deployment.yaml &
wait %1;
microk8s.kubectl delete -f config/storage.yaml &
wait %1;
microk8s.kubectl create -f config/storage.yaml &
wait %1;
microk8s.kubectl create -f config/service.yaml &
wait %1;
microk8s.kubectl create -f config/deployment.yaml &
wait %1;
microk8s.kubectl create -f config/ingress.yaml &



echo "======================================================================="
echo "Deployed Frontend App Service"
echo "======================================================================="