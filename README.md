# Mutant Detector API

API REST desarrollada con Spring Boot para detectar si un humano es mutante basándose en su secuencia de ADN. Un humano es considerado mutante si se encuentran más de una secuencia de cuatro letras iguales de forma horizontal, vertical o diagonal en su matriz de ADN NxN.

## Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
- [Endpoints de la API](#endpoints-de-la-api)
- [Ejemplos de Uso](#ejemplos-de-uso)
- [Arquitectura del Proyecto](#arquitectura-del-proyecto)
- [Base de Datos](#base-de-datos)
- [Testing](#testing)
- [Cobertura de Código](#cobertura-de-código)
- [Documentación de la API](#documentación-de-la-api)
- [Docker](#docker)
- [Tecnologías Utilizadas](#tecnologías-utilizadas)

## Descripción del Proyecto

Este proyecto implementa un sistema de detección de mutantes que analiza secuencias de ADN representadas como matrices NxN. El sistema identifica patrones específicos de bases nitrogenadas (A, T, C, G) y determina si el ADN corresponde a un mutante o a un humano normal.

### Criterios de Detección

Un humano es mutante si se encuentran **más de una secuencia** de cuatro letras iguales de forma:
- **Horizontal**: Secuencias en la misma fila
- **Vertical**: Secuencias en la misma columna
- **Diagonal descendente**: Secuencias de izquierda a derecha, de arriba a abajo
- **Diagonal ascendente**: Secuencias de izquierda a derecha, de abajo a arriba

### Características Principales

- Validación exhaustiva de secuencias de ADN
- Detección optimizada con early termination (termina al encontrar la segunda secuencia)
- Sistema de caché mediante hash SHA-256 para evitar recálculos
- Persistencia de resultados en base de datos H2
- Estadísticas en tiempo real de verificaciones realizadas
- Documentación interactiva con Swagger/OpenAPI
- Cobertura de tests del 98%

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+** o superior
- **Docker** (opcional, para ejecución containerizada)
- **Git** (para clonar el repositorio)

## Instalación y Configuración

### Clonar el Repositorio

```bash
git clone https://github.com/MatiasRossello/TrabajoFinal-Rossello-51075.git
cd TrabajoFinal-Rossello-51075/dnaRecord
```

### Compilar el Proyecto

```bash
./mvnw clean install
```

O en Windows:

```bash
mvnw.cmd clean install
```

## Ejecución de la Aplicación

### Método 1: Ejecutar con Maven

```bash
./mvnw spring-boot:run
```

### Método 2: Ejecutar el JAR compilado

```bash
java -jar target/mutantes-api-0.0.1-SNAPSHOT.jar
```

### Método 3: Ejecutar con Docker

```bash
# Construir la imagen
docker build -t mutant-detector-api .

# Ejecutar el contenedor
docker run -p 8080:8080 mutant-detector-api
```

La aplicación estará disponible en: `http://localhost:8080`

## Endpoints de la API

### 1. POST /mutant

Verifica si una secuencia de ADN corresponde a un mutante.

**Request:**
```json
POST /mutant
Content-Type: application/json

{
  "dna": [
    "ATGCGA",
    "CAGTGC",
    "TTATGT",
    "AGAAGG",
    "CCCCTA",
    "TCACTG"
  ]
}
```

**Responses:**

- **200 OK**: El ADN es mutante
  ```json
  (Sin contenido en el cuerpo)
  ```

- **403 FORBIDDEN**: El ADN es humano (no mutante)
  ```json
  (Sin contenido en el cuerpo)
  ```

- **400 BAD REQUEST**: ADN inválido (formato incorrecto)
  ```json
  {
    "timestamp": "2025-11-26T12:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "La matriz de ADN no es cuadrada",
    "path": "/mutant"
  }
  ```

**Validaciones:**

- La matriz debe ser cuadrada (NxN)
- El tamaño mínimo es 4x4
- Solo se permiten los caracteres: A, T, C, G (mayúsculas)
- No puede ser nula ni vacía
- Todas las filas deben tener la misma longitud

### 2. GET /stats

Obtiene estadísticas de las verificaciones de ADN realizadas.

**Request:**
```
GET /stats
```

**Response:**
```json
{
  "count_mutant_dna": 40,
  "count_human_dna": 100,
  "ratio": 0.4
}
```

**Descripción de campos:**

- `count_mutant_dna`: Cantidad total de ADN mutante detectado
- `count_human_dna`: Cantidad total de ADN humano detectado
- `ratio`: Ratio de mutantes sobre humanos (count_mutant_dna / count_human_dna)
  - Retorna 0 si no hay humanos detectados

## Ejemplos de Uso

### Ejemplo 1: ADN Mutante (2 secuencias horizontales)

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "AAAA",
      "CCCC",
      "TCAG",
      "GGTC"
    ]
  }'
```

**Respuesta:** `HTTP 200 OK`

**Análisis:**
- Fila 0: `AAAA` (secuencia horizontal de A)
- Fila 1: `CCCC` (secuencia horizontal de C)
- Total: 2 secuencias → ES MUTANTE

### Ejemplo 2: ADN Humano (1 sola secuencia)

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGC",
      "ATGC",
      "ATGC",
      "ATGC"
    ]
  }'
```

**Respuesta:** `HTTP 403 FORBIDDEN`

**Análisis:**
- Columna 0: `AAAA` (secuencia vertical de A)
- Total: 1 secuencia → NO ES MUTANTE

### Ejemplo 3: ADN Mutante (secuencias diagonales)

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGCGA",
      "CAGTGC",
      "TTATGT",
      "AGAAGG",
      "CCCCTA",
      "TCACTG"
    ]
  }'
```

**Respuesta:** `HTTP 200 OK`

**Análisis:**
- Fila 4: `CCCCTA` (secuencia horizontal de C)
- Diagonal: Posiciones (0,0), (1,1), (2,2), (3,3) → `AAGT` + continúa
- Total: 2+ secuencias → ES MUTANTE

### Ejemplo 4: Obtener Estadísticas

```bash
curl -X GET http://localhost:8080/stats
```

**Respuesta:**
```json
{
  "count_mutant_dna": 3,
  "count_human_dna": 7,
  "ratio": 0.42857142857142855
}
```

### Ejemplo 5: ADN Inválido

```bash
curl -X POST http://localhost:8080/mutant \
  -H "Content-Type: application/json" \
  -d '{
    "dna": [
      "ATGC",
      "ATGC",
      "ATGC"
    ]
  }'
```

**Respuesta:** `HTTP 400 BAD REQUEST`
```json
{
  "timestamp": "2025-11-26T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "La matriz de ADN no es cuadrada",
  "path": "/mutant"
}
```

## Arquitectura del Proyecto

El proyecto sigue una arquitectura en capas basada en los principios SOLID:

```
com.example.utn.dnaRecord/
│
├── controller/          # Capa de presentación (REST Controllers)
│   ├── MutantController.java
│   └── GlobalExceptionHandler.java
│
├── service/            # Capa de lógica de negocio
│   ├── MutantService.java       # Servicio principal (cache + coordinación)
│   ├── MutantDetector.java      # Algoritmo de detección puro
│   └── StatsService.java        # Servicio de estadísticas
│
├── repository/         # Capa de acceso a datos
│   └── DnaRecordRepository.java
│
├── entity/            # Entidades JPA
│   └── DnaRecord.java
│
├── dto/               # Objetos de transferencia de datos
│   ├── DnaRequestDTO.java
│   └── StatsResponseDTO.java
│
├── validator/         # Validadores personalizados
│   ├── ValidDna.java           # Anotación de validación
│   └── DnaValidator.java       # Lógica de validación
│
├── exception/         # Excepciones personalizadas
│   ├── DnaHashCalculationException.java
│   └── GlobalExceptionHandler.java
│
└── config/            # Configuración de la aplicación
    └── OpenApiConfig.java       # Configuración de Swagger
```

### Descripción de Componentes

#### Controller Layer
- **MutantController**: Expone los endpoints REST `/mutant` y `/stats`
- **GlobalExceptionHandler**: Manejo centralizado de excepciones con respuestas HTTP apropiadas

#### Service Layer
- **MutantService**: Coordina la detección de mutantes, gestiona el caché y persiste resultados
- **MutantDetector**: Implementa el algoritmo de detección de secuencias (lógica pura sin dependencias)
- **StatsService**: Calcula estadísticas desde la base de datos

#### Repository Layer
- **DnaRecordRepository**: Interfaz JPA para operaciones CRUD con la base de datos

#### Entity Layer
- **DnaRecord**: Entidad JPA que representa un registro de ADN en la base de datos

#### DTO Layer
- **DnaRequestDTO**: DTO para recibir secuencias de ADN del cliente
- **StatsResponseDTO**: DTO para responder con estadísticas

#### Validator Layer
- **ValidDna**: Anotación personalizada para validar secuencias de ADN
- **DnaValidator**: Implementación de la lógica de validación

#### Exception Layer
- **DnaHashCalculationException**: Excepción para errores en el cálculo de hash SHA-256

#### Config Layer
- **OpenApiConfig**: Configuración de Swagger/OpenAPI para documentación interactiva

## Base de Datos

### Configuración

La aplicación utiliza **H2 Database** en modo in-memory para persistencia de datos.

**Configuración en `application.properties`:**

```properties
spring.datasource.url=jdbc:h2:mem:mutantdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.hibernate.ddl-auto=update
```

### Consola H2

Para acceder a la consola de administración de la base de datos:

1. Navegar a: `http://localhost:8080/h2-console`
2. Usar las siguientes credenciales:
   - **JDBC URL**: `jdbc:h2:mem:mutantdb`
   - **User Name**: `sa`
   - **Password**: (dejar vacío)

### Esquema de Base de Datos

**Tabla: `dna_record`**

| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | BIGINT | Clave primaria (auto-incremental) |
| dna_hash | VARCHAR(64) | Hash SHA-256 de la secuencia de ADN (único) |
| is_mutant | BOOLEAN | Indica si el ADN es mutante (true) o humano (false) |

**Índices:**
- Índice único en `dna_hash` para optimizar búsquedas y evitar duplicados

### Sistema de Caché

El sistema implementa un caché basado en hash SHA-256:

1. Cuando se recibe una secuencia de ADN, se calcula su hash SHA-256
2. Se busca el hash en la base de datos
3. Si existe, se retorna el resultado cacheado (sin recalcular)
4. Si no existe, se ejecuta el algoritmo de detección y se guarda el resultado

**Beneficios:**
- Evita procesamiento redundante de ADN ya analizado
- Mejora significativa en tiempo de respuesta para secuencias repetidas
- Garantiza consistencia en los resultados

## Testing

El proyecto cuenta con una suite completa de tests que cubren todos los componentes.

### Ejecutar Tests

```bash
./mvnw test
```

### Ejecutar Tests con Reporte de Cobertura

```bash
./mvnw test jacoco:report
```

El reporte HTML se genera en: `target/site/jacoco/index.html`

### Tipos de Tests

#### 1. Tests Unitarios

**MutantDetectorTest** (24 tests)
- Tests de matrices nulas y vacías
- Tests de matrices inválidas (no cuadradas, caracteres inválidos)
- Tests de detección de secuencias horizontales
- Tests de detección de secuencias verticales
- Tests de detección de secuencias diagonales
- Tests de early termination

**MutantServiceTest** (4 tests)
- Análisis de ADN mutante (con caché)
- Análisis de ADN humano (con caché)
- Verificación de persistencia en base de datos
- Manejo de excepciones en cálculo de hash

**StatsServiceTest** (6 tests)
- Estadísticas sin datos
- Estadísticas solo con mutantes
- Estadísticas solo con humanos
- Estadísticas mixtas (mutantes y humanos)
- Cálculo correcto del ratio

**DnaValidatorTest** (11 tests)
- Validación de matrices válidas
- Validación de matrices nulas
- Validación de matrices vacías
- Validación de matrices no cuadradas
- Validación de caracteres inválidos
- Validación de tamaños mínimos

**GlobalExceptionHandlerTest** (5 tests)
- Manejo de IllegalArgumentException
- Manejo de MethodArgumentNotValidException
- Manejo de excepciones genéricas

**DnaHashCalculationExceptionTest** (2 tests)
- Constructor con mensaje
- Constructor con mensaje y causa

#### 2. Tests de Cobertura

**MutantDetectorCoverageTest** (17 tests)
- Tests específicos para cubrir todas las ramas del algoritmo
- Tests de límites y casos edge
- Tests de continuación de secuencias
- Tests de secuencias en diferentes posiciones

#### 3. Tests de Integración

**MutantControllerIntegrationTest** (8 tests)
- Tests end-to-end del endpoint `/mutant`
- Tests end-to-end del endpoint `/stats`
- Tests de validación de request body
- Tests de respuestas HTTP correctas

#### 4. Tests de Performance

**MutantDetectorPerformanceTest** (7 tests)
- Performance con matrices 6x6 (< 1ms esperado)
- Performance con matrices 100x100 (< 20ms esperado)
- Performance con matrices 1000x1000 (< 500ms esperado)
- Tests de throughput
- Tests de complejidad algorítmica

### Total de Tests: 92

## Cobertura de Código

El proyecto alcanza una cobertura del **98%** medida con JaCoCo.

### Métricas Globales

| Métrica | Cobertura |
|---------|-----------|
| **Instrucciones** | 98% (479/487) |
| **Ramas** | 94% (72/76) |
| **Complejidad** | 95% (54/57) |
| **Líneas** | 98% (91/93) |
| **Métodos** | 100% (19/19) |
| **Clases** | 100% (8/8) |

### Cobertura por Paquete

| Paquete | Instrucciones | Ramas |
|---------|---------------|--------|
| **controller** | 100% | 100% |
| **entity** | 100% | n/a |
| **exception** | 100% | n/a |
| **service** | 98% | 95% |
| **validator** | 96% | 91% |

### Configuración de JaCoCo

El proyecto está configurado con un threshold mínimo del 80% de cobertura de líneas a nivel de paquete. El build falla si no se cumple este requisito.

```xml
<execution>
    <id>jacoco-check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>PACKAGE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

## Documentación de la API

La API está documentada usando **OpenAPI 3.0** (Swagger).

### Acceder a la Documentación

Una vez que la aplicación esté ejecutándose, acceder a:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

### Características de la Documentación

- Descripción detallada de cada endpoint
- Ejemplos de request y response
- Esquemas de validación
- Posibilidad de probar los endpoints directamente desde el navegador
- Códigos de respuesta HTTP documentados

### Probar Endpoints desde Swagger

1. Navegar a `http://localhost:8080/swagger-ui.html`
2. Seleccionar el endpoint deseado
3. Hacer clic en "Try it out"
4. Ingresar los datos de prueba
5. Hacer clic en "Execute"
6. Ver la respuesta del servidor

## Docker

El proyecto incluye un Dockerfile optimizado con multi-stage build.

### Características del Dockerfile

- **Stage 1 (Build)**: Usa imagen Maven con JDK 21 para compilar
- **Stage 2 (Runtime)**: Usa imagen JRE 21 Alpine (más liviana)
- Reduce el tamaño de la imagen final
- Optimiza los tiempos de build con caché de capas

### Construir la Imagen

```bash
docker build -t mutant-detector-api:latest .
```

### Ejecutar el Contenedor

```bash
docker run -p 8080:8080 mutant-detector-api:latest
```

### Ejecutar en Background

```bash
docker run -d -p 8080:8080 --name mutant-api mutant-detector-api:latest
```

### Ver Logs

```bash
docker logs -f mutant-api
```

### Detener el Contenedor

```bash
docker stop mutant-api
docker rm mutant-api
```

## Tecnologías Utilizadas

### Backend
- **Java 21**: Lenguaje de programación
- **Spring Boot 3.5.7**: Framework principal
- **Spring Web**: Para crear APIs REST
- **Spring Data JPA**: Para acceso a datos
- **Spring Validation**: Para validación de datos

### Base de Datos
- **H2 Database**: Base de datos en memoria

### Documentación
- **SpringDoc OpenAPI 2.8.14**: Generación automática de documentación Swagger

### Testing
- **JUnit 5**: Framework de testing
- **Mockito**: Mocking framework
- **Spring Boot Test**: Testing de integración
- **JaCoCo 0.8.11**: Cobertura de código

### Herramientas
- **Maven**: Gestión de dependencias y build
- **Lombok**: Reducción de código boilerplate
- **Docker**: Containerización

### Seguridad y Validación
- **Jakarta Validation**: Bean Validation API
- **Custom Validators**: Validadores personalizados para ADN

## Algoritmo de Detección

### Descripción del Algoritmo

El algoritmo implementado en `MutantDetector.java` utiliza las siguientes optimizaciones:

1. **Validación temprana**: Verifica matriz nula, vacía, cuadrada y caracteres válidos antes de buscar secuencias
2. **Early termination**: Termina la búsqueda al encontrar la segunda secuencia (no es necesario seguir buscando)
3. **Búsqueda direccional optimizada**: Verifica solo posiciones válidas para cada dirección
4. **Comparación directa**: Usa comparación directa de caracteres sin loops adicionales

### Direcciones de Búsqueda

| Dirección | Delta Row | Delta Col | Descripción |
|-----------|-----------|-----------|-------------|
| Horizontal | 0 | 1 | Misma fila, columnas consecutivas |
| Vertical | 1 | 0 | Misma columna, filas consecutivas |
| Diagonal \ | 1 | 1 | Diagonal descendente (izq-arriba a der-abajo) |
| Diagonal / | -1 | 1 | Diagonal ascendente (izq-abajo a der-arriba) |

### Complejidad

- **Complejidad temporal**: O(N²) en el peor caso, pero con early termination típicamente O(N²/k) donde k depende de la distribución de secuencias
- **Complejidad espacial**: O(N²) para almacenar la matriz como array de caracteres

### Ejemplo de Ejecución

Para la matriz:
```
A T G C
A T G C
A T G C
A T G C
```

1. Se convierte a matriz de caracteres 4x4
2. Se recorre cada posición (0,0) hasta (3,3)
3. Desde (0,0):
   - Horizontal: A-T-G-C (no hay secuencia)
   - Vertical: A-A-A-A (SECUENCIA ENCONTRADA, count=1)
   - Diagonales: No aplicables o sin secuencia
4. Se continúa buscando hasta encontrar una segunda secuencia o terminar
5. Resultado: count=1, retorna false (NO ES MUTANTE)

## Autor

**Matias Rossello**
- GitHub: [MatiasRossello](https://github.com/MatiasRossello)
- Proyecto: Trabajo Final - Desarrollo de Software
- Universidad Tecnológica Nacional (UTN)

## Licencia

Este proyecto fue desarrollado como trabajo final para la materia Desarrollo de Software de la Universidad Tecnológica Nacional.
