#!/bin/bash
microk8s.kubectl  create -f config/storage.yaml &
microk8s.kubectl  create -f config/secrets.yaml &
microk8s.kubectl  create -f config/deployment.yaml&  
microk8s.kubectl  create -f config/service.yaml &
