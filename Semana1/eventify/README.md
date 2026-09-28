# Eventify — Estructura Inicial (Spring MVC)

Base tecnica del catalogo de **Venues** y **Events**, con datos en memoria,
documentacion Swagger y pruebas unitarias, siguiendo la HU indicada.

## Como ejecutar

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/events` y `http://localhost:8080/api/venues`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Como correr los tests

```bash
mvn test
```

## Arquitectura (Estereotipos / Spring MVC)

```
Controller (@RestController)  -->  Service (@Service)  -->  Repository (@Repository)
        |                                |                          |
   HTTP + Swagger                 Validaciones de negocio     Coleccion en memoria
```

- **Inyeccion de dependencias**: 100% por constructor (Controller -> Service -> Repository).
- **Config (`@Configuration` + `@Bean`)**:
  - `DataSeederConfig`: carga datos iniciales via `CommandLineRunner`, controlado por
    la propiedad `eventify.seeder.enabled` (true/false).
  - `OpenApiConfig`: personaliza titulo/descripcion de Swagger.
- **Manejo de errores**: `GlobalExceptionHandler` (`@RestControllerAdvice`) traduce
  `InvalidDataException` en `400 Bad Request` con un cuerpo JSON legible.

## Mapeo a criterios de aceptacion

| Escenario | Como se cumple |
|---|---|
| 1. Registro exitoso | `POST /api/events` -> `EventService.createEvent` valida y guarda -> Controller retorna `201 Created` |
| 2. Registro invalido | Nombre vacio -> `EventService` lanza `InvalidDataException` -> `GlobalExceptionHandler` retorna `400` sin tocar el repositorio |
| 3. Catalogo vacio | Con `eventify.seeder.enabled=false`, `GET /api/events` retorna `[]` con `200 OK` |
| 4. Documentacion | `/swagger-ui.html` lista los endpoints `GET`/`POST` de los tags "Events" y "Venues", listos para probar |

## Probar el Escenario 3 (seeder desactivado)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--eventify.seeder.enabled=false
```

## Ejemplos de peticiones

**Crear evento**
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Feria Tech","date":"2026-12-01","description":"Feria de tecnologia"}'
```

**Crear venue**
```bash
curl -X POST http://localhost:8080/api/venues \
  -H "Content-Type: application/json" \
  -d '{"name":"Coliseo Central","address":"Cra 50 #80-20","capacity":1200}'
```
