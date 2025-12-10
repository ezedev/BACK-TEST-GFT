# 🛒 Similar Products Service — README (Nivel Arquitecto)

> Servicio reactivo que devuelve productos similares a uno dado.  
> Arquitectura Hexagonal, Spring WebFlux, Resilience4j, Caffeine, MapStruct, testing avanzado.

---

## 📌 Resumen del proyecto

Este microservicio ofrece un endpoint:

```
GET /product/{id}/similar
```

Devuelve un `Flux<ProductDTO>` con los productos similares al `id` solicitado.  
El servicio consulta un servicio externo (API de productos) y aplica caching, tolerancia a fallos y validaciones.

---

## 🧱 Arquitectura (Hexagonal / Ports & Adapters)

```
src/main/java
└─ com.inditex.site
   ├─ domain
   │  ├─ model
   │  ├─ exception
   │  └─ port
   │     ├─ in  (GetSimilarProductsUseCase)
   │     └─ out (ProductClientPort)
   ├─ application
   │  └─ usecase (GetSimilarProductsService)
   └─ infrastructure
      ├─ adapter
      │  ├─ in.rest.controller (SimilarProductsController)
      │  └─ out.client (ProductClientAdapter)
      └─ config (WebClientConfig, CacheConfig, etc.)
```

- Las dependencias del proyecto fluyen **hacia adentro** (hacia `domain`).
- Los tests de arquitectura (ArchUnit) validan el cumplimiento de esas reglas.

---

## 🧰 Stack tecnológico

- **Java 17+ / 21**
- **Spring Boot 3.x**
- **Open APi** (`Swagger`)
- **Spring WebFlux** (WebClient + Reactor)
- **Project Reactor** (`Mono`, `Flux`)
- **Resilience4j** (circuit-breaker, retry)
- **Caffeine** (cache en memoria)
- **MapStruct** (mapeo DTO ↔ Domain)
- **Lombok** (reducción de boilerplate)
- **Spring Actuator** (observability)
- **springdoc-openapi** (swagger ui)
- **JUnit 5, Mockito, Reactor Test, RestAssured** (testing)
- **ArchUnit** (tests arquitectónicos)
- **Docker / docker-compose** (mocks e infra del enunciado)

---

## Decisiones técnicas (resumidas)

- **Reactive (WebFlux):** IO no bloqueante, mejor escalado bajo alta concurrencia, composición asíncrona de múltiples llamadas externas.
- **Mono / Flux:** `Mono<T>` = 0..1 elemento, `Flux<T>` = 0..N elementos. Usados coherentemente según semántica del dato.
- **Resilience4j:** proteger contra fallos de dependencias externas con circuit breaker + retry, y definir fallbacks controlados.
- **Caffeine:** cache local con TTL para reducir latencia y carga de las APIs externas.
- **MapStruct:** mapeos sencillos y eficientes para separar DTOs (API) del dominio interno.
- **Hexagonal:** testabilidad, independencia de infra y facilidad para sustituir adaptadores.

---

## 🧪 Estructura de tests (src/test/java)

- `com.inditex.site.application.usecase` → unit tests de use cases (Mockito + StepVerifier)
- `com.inditex.site.infrastructure.adapter.in.rest.controller` → unit tests de controllers (WebFluxTest o Mockito puro)
- `com.inditex.site.infrastructure.adapter.out.client` → unit tests de adapters (mock WebClient)
- `com.inditex.site.architecture` → ArchUnit tests (reglas de capas)
- `com.inditex.site.contract` → tests contract / API-first (validación OpenAPI)
- `com.inditex.site.e2e` → tests end-to-end (RestAssured; se puede mockear ProductClientPort o usar MockWebServer)

> **Nota:** ArchUnit se configura para **NO** analizar `src/test/java` (import option `DoNotIncludeTests`) — así los tests con Mockito/JUnit no rompen las reglas.

---

## ⚙️ Archivos importantes de configuración

### `application.yml` (ejemplo)

```yaml
server:
  port: 5000

spring:
  application:
    name: similar-products-service

external:
  product:
    baseUrl: http://localhost:8081  

resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 5
        minimumNumberOfCalls: 3
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 2
    instances:
      productClient:
        baseConfig: default
  retry:
    instances:
      productClient:
        maxAttempts: 3
        waitDuration: 1s

spring:
  cache:
    type: caffeine

caffeine:
  spec: maximumSize=1000,expireAfterWrite=5m
```

---

## 🚨 Excepciones y manejo de errores 

- `ProductNotFoundException` → dominio (404, mapeado en controller advice)
- `SimilarProductsUnavailableException` → dominio (404 o 204 según política)
- `ProductClientAdapterException` → infraestructura (mapped a 503)

---

## 📄 OpenAPI / API-First

- El proyecto incluye `src/main/resources/openapi/similar-products.yaml`.
- Se usa `openapi-generator-maven-plugin` para generar DTOs y stubs (config en `pom.xml`).

### Cómo generar los DTOs desde OpenAPI

```bash
mvn clean generate-sources
```

- Los modelos y APIs generadas aparecerán en `target/generated-sources/openapi`.

---

## 🏃 Comandos — desarrollo y ejecución

### Requisitos previos

- Java 17+ (o 21 según `pom.xml`)
- Maven 3.8+
- Docker & docker-compose

### 1) Arrancar infra del enunciado (mocks/influx/grafana)

```bash
docker compose up -d simulado influxdb grafana
```

- Verificar:

```bash
docker ps
curl http://localhost:3001/product/1/similarids
```

### 2) Compilar proyecto

```bash
mvn clean install -DskipTests=false
```

### 3) Ejecutar local

```bash
mvn spring-boot:run
# o
java -jar target/<artifact>-<version>.jar
```
http://localhost:5000/swagger-ui/index.html

- La app corre por defecto en `http://localhost:5000`.
- Endpoint: `GET http://localhost:5000/product/{id}/similar`

---

## 🧪 Testing — cómo ejecutar y qué cubre

1. **Todos los tests (unit, arch, contract, e2e)**

```bash
mvn clean test
```

2. **Unit tests solo**

```bash
mvn -Dtest=**/*Test test
# o
mvn -Dtest=GetSimilarProductsServiceTest test
```

3. **Test de arquitectura (ArchUnit)**

- ArchUnit está configurado para no incluir `src/test/java`.
- Ejecutarlos:

```bash
mvn -Dtest=com.inditex.site.architecture.HexagonalArchitectureTest test
```

4. **Contract tests (API-First)**

```bash
mvn -Dtest=com.inditex.site.contract.** test
```

---

## ❓ FAQ (preguntas frecuentes)

**Q:** ¿Por qué no usar RestTemplate?  
**A:** RestTemplate es bloqueante. Para servicios que agregan múltiples llamadas externas y requieren escalabilidad, WebFlux/Project Reactor es más apropiado.

**Q:** ¿Por qué Caffeine y no Redis?  
**A:** Caffeine es simple y ultra rápido para cache local en un único pod/instancia. Si necesitás coherencia entre instancias, combinar con Redis es opción futura.

**Q:** ¿Por qué Resilience4j y no Hystrix?  
**A:** Hystrix está en mantenimiento; Resilience4j es moderno, modular y soporta programación reactiva con Reactor.
