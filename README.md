# E-Commerce API Gateway

This repository contains the **API Gateway** for the **E-Commerce Microservices Application**.  
It acts as a **single entry point** for all client requests and handles **JWT authentication, role-based access control, and routing** to the microservices.

---

## Overview

- Central entry point for all client requests
- Routes requests to:
    - `auth-service`
    - `product-service`
    - `order-service`
    - `payment-service`
- Performs **JWT validation** and extracts user roles
- Blocks unauthorized access to protected endpoints
- Works with **Eureka Service** for service discovery

---

## Tech Stack

- Java 17
- Spring Boot 
- Spring Cloud Gateway
- Spring Security + JWT
- Eureka Discovery Client

---


