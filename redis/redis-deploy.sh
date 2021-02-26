#!/bin/bash
echo "Deploying Redis Service"
microk8s.kubectl create -f config/deployment.yaml &
microk8s.kubectl create -f config/service.yaml