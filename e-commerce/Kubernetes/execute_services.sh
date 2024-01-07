#!/bin/bash

# Define an array of service names
services=("rabbitmq-service" "eureka-server " "spring-cloud-gateway " "email-service" "auth-service" "user-service" "wallet-service" "products-service" "orders-service" "payment-service" "shipping-service" "reviews-service")  # Add your service names here

# Loop through the array and execute 'minikube service' for each service
for service in "${services[@]}"
do
    minikube service "$service" &
done

# Wait for background processes to finish
wait
