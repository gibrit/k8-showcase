#!/bin/bash
microk8s.kubectl delete -f config/ingress.yaml &
microk8s.kubectl delete -f config/service.yaml&
microk8s.kubectl delete -f config/deployment.yaml &
microk8s.kubectl delete -f config/configmap.yaml&
microk8s.kubectl delete -f config/storage.yaml &

