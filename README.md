# Pluggable Service Discovery Demo

This repository demonstrates a Spring Boot microservices flow where service discovery changes by environment, but the business code does not.

The repository no longer uses a Maven multi-module reactor. Every folder is an independent Maven project with its own `pom.xml`.

## 1. What This Demo Does

The main business flow is:

```text
Client
  -> order-service
       -> inventory-service
       -> payment-service
            -> notification-service
```

Main entrypoint:

```text
POST /orders/{orderId}/process?sku=ABC123
```

Flow summary:

1. A client calls `order-service`
2. `order-service` checks stock in `inventory-service`
3. If stock is available, `order-service` calls `payment-service`
4. `payment-service` simulates a charge
5. `payment-service` calls `notification-service`
6. `order-service` returns a combined response

## 2. Upstream And Downstream Service Relationships

| Service | Upstream Of | Downstream Of | Notes |
|---|---|---|---|
| `order-service` | `inventory-service`, `payment-service` | external caller/client | Main orchestration service |
| `payment-service` | `notification-service` | `order-service` | Handles payment and confirmation trigger |
| `inventory-service` | none in this demo | `order-service` | Stock lookup only |
| `notification-service` | none in this demo | `payment-service` | Final notification step |
| `discovery-server-eureka` | registration/discovery infrastructure only | services in `eureka` profile | Used only in Eureka mode |

Inline code comments were added in the caller flow so it is easy to see the upstream/downstream connections in:

- `order-service/src/main/java/com/example/order/service/OrderProcessingService.java`
- `order-service/src/main/java/com/example/order/client/InventoryClient.java`
- `order-service/src/main/java/com/example/order/client/PaymentClient.java`
- `payment-service/src/main/java/com/example/payment/service/PaymentProcessingService.java`
- `payment-service/src/main/java/com/example/payment/client/NotificationClient.java`

## 3. Repository Layout

```text
common-discovery-support/
|-- README.md
|-- docker-compose.yml
|-- common-discovery-api/
|   |-- pom.xml
|   `-- src/
|-- service-discovery-support/
|   |-- pom.xml
|   `-- src/
|-- order-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|-- payment-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|-- inventory-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|-- notification-service/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
|-- discovery-server-eureka/
|   |-- pom.xml
|   |-- Dockerfile
|   `-- src/
`-- k8s/
    |-- namespace.yaml
    |-- order-service.yaml
    |-- payment-service.yaml
    |-- inventory-service.yaml
    `-- notification-service.yaml
```

## 4. Shared Modules

### `common-discovery-api`

This module contains only the shared discovery abstraction:

- `ServiceEndpointResolver`
- `ServiceResolutionException`

### `service-discovery-support`

This module contains reusable resolver implementations:

- `StaticServiceEndpointResolver`
- `DiscoveryClientServiceEndpointResolver`
- `KubernetesDnsServiceEndpointResolver`

This module has no controllers and no business logic.

## 5. Standalone Maven Project Pattern

Every module has its own standalone `pom.xml`.

Example shared library coordinates:

```xml
<groupId>com.example</groupId>
<artifactId>common-discovery-api</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

Example service dependency usage:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>common-discovery-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
    <groupId>com.example</groupId>
    <artifactId>service-discovery-support</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 6. Ports Used In Local Development

| Application | Port |
|---|---|
| `order-service` | `8080` |
| `payment-service` | `8081` |
| `inventory-service` | `8082` |
| `notification-service` | `8083` |
| `discovery-server-eureka` | `8761` |

## 7. Prerequisites

Install these tools before running the demo:

- Java 21
- Maven 3.9+
- Docker Desktop for Docker/Compose mode
- `kubectl` and `kind` for Kubernetes mode

## 8. Step-By-Step Build Instructions

Run all commands from the repository root unless a step says otherwise.

### Step 1: Install The Shared Libraries

These two modules must be installed first because `order-service` and `payment-service` depend on them as normal Maven artifacts.

```powershell
Set-Location common-discovery-api
mvn clean install
```

```powershell
Set-Location ../service-discovery-support
mvn clean install
```

### Step 2: Build Each Standalone Service

```powershell
Set-Location ../inventory-service
mvn clean install
```

```powershell
Set-Location ../notification-service
mvn clean install
```

```powershell
Set-Location ../payment-service
mvn clean install
```

```powershell
Set-Location ../order-service
mvn clean install
```

```powershell
Set-Location ../discovery-server-eureka
mvn clean install
```

### Step 3: Return To Repository Root

```powershell
Set-Location ..
```

At this point every project is independently buildable and installed in the local Maven repository.

## 9. Step-By-Step Local Run: `static-discovery`

Use this mode for local development without Eureka.

### How Static Mode Connects Services

Connection setup is manual and happens in config files:

- `order-service/src/main/resources/application-static-discovery.yml`
- `payment-service/src/main/resources/application-static-discovery.yml`

Those files define the downstream URLs:

- `order-service` -> `inventory-service`
- `order-service` -> `payment-service`
- `payment-service` -> `notification-service`

### Step 1: Start `inventory-service`

```powershell
Set-Location inventory-service
mvn spring-boot:run "-Dspring-boot.run.profiles=static-discovery"
```

### Step 2: Start `notification-service`

Open a new terminal:

```powershell
Set-Location notification-service
mvn spring-boot:run "-Dspring-boot.run.profiles=static-discovery"
```

### Step 3: Start `payment-service`

Open a new terminal:

```powershell
Set-Location payment-service
mvn spring-boot:run "-Dspring-boot.run.profiles=static-discovery"
```

### Step 4: Start `order-service`

Open a new terminal:

```powershell
Set-Location order-service
mvn spring-boot:run "-Dspring-boot.run.profiles=static-discovery"
```

### Step 5: Test The Happy Path

Open another terminal:

```powershell
curl.exe -X POST "http://localhost:8080/orders/ORD-100/process?sku=ABC123"
```

Expected behavior:

- `order-service` receives the client call
- `order-service` calls downstream `inventory-service`
- `order-service` then calls downstream `payment-service`
- `payment-service` calls downstream `notification-service`
- a combined JSON response is returned

### Step 6: Test The Out-Of-Stock Path

```powershell
curl.exe -X POST "http://localhost:8080/orders/ORD-101/process?sku=OOS-001"
```

Expected behavior:

- `order-service` still calls `inventory-service`
- `inventory-service` reports no stock
- `order-service` does not call `payment-service`
- response status in the payload becomes `OUT_OF_STOCK`

## 10. Step-By-Step Local Run: `eureka`

Use this mode when you want application-level registration and discovery.

### How Eureka Mode Connects Services

Registration happens automatically from each service's `application-eureka.yml`.

The important settings are:

- `eureka.client.enabled=true`
- `eureka.client.register-with-eureka=true`
- `eureka.client.fetch-registry=true`
- `eureka.client.service-url.defaultZone=http://localhost:8761/eureka/`

The business classes still do not use `EurekaClient` directly. The switching happens through the `ServiceEndpointResolver` bean wiring.

### Step 1: Start Eureka Server

```powershell
Set-Location discovery-server-eureka
mvn spring-boot:run
```

Optional verification:

- Open `http://localhost:8761`

### Step 2: Start `inventory-service`

Open a new terminal:

```powershell
Set-Location inventory-service
mvn spring-boot:run "-Dspring-boot.run.profiles=eureka"
```

### Step 3: Start `notification-service`

Open a new terminal:

```powershell
Set-Location notification-service
mvn spring-boot:run "-Dspring-boot.run.profiles=eureka"
```

### Step 4: Start `payment-service`

Open a new terminal:

```powershell
Set-Location payment-service
mvn spring-boot:run "-Dspring-boot.run.profiles=eureka"
```

### Step 5: Start `order-service`

Open a new terminal:

```powershell
Set-Location order-service
mvn spring-boot:run "-Dspring-boot.run.profiles=eureka"
```

### Step 6: Verify The Service Flow

```powershell
curl.exe -X POST "http://localhost:8080/orders/ORD-200/process?sku=ABC123"
```

Expected behavior:

- all services register with Eureka
- `order-service` resolves downstream services through `DiscoveryClientServiceEndpointResolver`
- business code remains unchanged

## 11. Step-By-Step Run: `k8s`

Use this mode when running on Kubernetes without Eureka.

### How Kubernetes Mode Connects Services

No app-level registration happens in Kubernetes mode.

Why:

- Kubernetes `Service` resources create stable DNS names
- the resolver converts a logical service name into DNS such as `http://payment-service`
- the application still depends only on `ServiceEndpointResolver`

### Step 1: Build Docker Images

```powershell
docker build -f order-service/Dockerfile -t discovery-demo/order-service:local .
docker build -f payment-service/Dockerfile -t discovery-demo/payment-service:local .
docker build -f inventory-service/Dockerfile -t discovery-demo/inventory-service:local .
docker build -f notification-service/Dockerfile -t discovery-demo/notification-service:local .
```

### Step 2: Create A Local Cluster

```powershell
kind create cluster --name discovery-demo
```

### Step 3: Load Images Into `kind`

```powershell
kind load docker-image discovery-demo/order-service:local discovery-demo/payment-service:local discovery-demo/inventory-service:local discovery-demo/notification-service:local --name discovery-demo
```

### Step 4: Apply Kubernetes Manifests

```powershell
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/inventory-service.yaml
kubectl apply -f k8s/notification-service.yaml
kubectl apply -f k8s/payment-service.yaml
kubectl apply -f k8s/order-service.yaml
```

### Step 5: Check Pod Startup

```powershell
kubectl -n discovery-demo get pods
```

### Step 6: Expose `order-service`

```powershell
kubectl -n discovery-demo port-forward service/order-service 8080:80
```

### Step 7: Test The Flow

```powershell
curl.exe -X POST "http://localhost:8080/orders/ORD-300/process?sku=ABC123"
```

Expected behavior:

- Kubernetes DNS handles downstream addressing
- no Eureka server is required
- the same business code path still runs

## 12. Step-By-Step Run With Docker Compose

### Static Mode In Compose

```powershell
$env:DISCOVERY_MODE="static-discovery"
docker compose up --build
```

Compose still uses static config in this mode. The difference is that the configured downstream URLs point to Docker service DNS names such as:

- `http://inventory-service:8082`
- `http://payment-service:8081`
- `http://notification-service:8083`

### Eureka Mode In Compose

```powershell
$env:DISCOVERY_MODE="eureka"
docker compose --profile eureka up --build
```

In this mode the same containers start with the `eureka` profile instead of `static-discovery`.

## 13. API Endpoints

| Service | Endpoint | Purpose |
|---|---|---|
| `order-service` | `POST /orders/{orderId}/process?sku=ABC123` | Main orchestration endpoint |
| `inventory-service` | `GET /inventory/{sku}/availability` | Stock lookup |
| `payment-service` | `POST /payments/{orderId}/charge?sku=ABC123` | Payment processing |
| `notification-service` | `POST /notifications` | Send notification |

Direct examples:

```powershell
curl.exe "http://localhost:8082/inventory/ABC123/availability"
curl.exe -X POST "http://localhost:8081/payments/ORD-400/charge?sku=ABC123"
curl.exe -X POST "http://localhost:8083/notifications" -H "Content-Type: application/json" -d "{\"orderId\":\"ORD-500\",\"channel\":\"email\",\"message\":\"Manual notification\"}"
```

## 14. How Discovery Changes Without Business Code Changes

The strategy contract remains:

```java
public interface ServiceEndpointResolver {
    URI resolve(String serviceName);
}
```

Only the resolver implementation changes by profile:

- `static-discovery` -> `StaticServiceEndpointResolver`
- `eureka` -> `DiscoveryClientServiceEndpointResolver`
- `k8s` -> `KubernetesDnsServiceEndpointResolver`

What does not change:

- controller code
- service code
- client code
- upstream/downstream call chain
- service names

## 15. Troubleshooting

### Problem: `order-service` or `payment-service` cannot resolve shared dependencies

Fix:

1. Reinstall `common-discovery-api`
2. Reinstall `service-discovery-support`
3. Rebuild the failing service

### Problem: Port already in use

Fix:

- stop the service already running on `8080`, `8081`, `8082`, `8083`, or `8761`

### Problem: Eureka dashboard shows no instances

Fix:

1. make sure `discovery-server-eureka` started first
2. confirm the service is running with profile `eureka`
3. confirm `application-eureka.yml` points to `http://localhost:8761/eureka/`

### Problem: Kubernetes flow cannot reach downstream services

Fix:

1. confirm all `Service` objects exist in namespace `discovery-demo`
2. confirm pods are ready
3. confirm applications are running with profile `k8s`

## 16. Final Design Notes

- `order-service` is the main upstream orchestrator inside the demo
- `payment-service` becomes an upstream caller only for `notification-service`
- `inventory-service` and `notification-service` are downstream endpoints in this workflow
- connection technology changes by environment
- business behavior stays the same
