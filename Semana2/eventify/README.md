# Eventify — Persistencia Real, CRUD, Paginación (Spring MVC + JPA)

Plataforma de catálogo de **Venues** y **Events**, con persistencia real
(Spring Data JPA + Hibernate sobre H2 en archivo), CRUD completo,
paginación/ordenamiento, documentación Swagger y pruebas unitarias e
integración.

## Como ejecutar

```bash
mvn spring-boot:run
```

- API: `http://localhost:8080/api/events` y `http://localhost:8080/api/venues`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- Consola H2 (para inspeccionar los datos persistidos): `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/eventifydb;AUTO_SERVER=TRUE`
  - Usuario: `sa` / Password: *(vacio)*

## Como correr los tests

```bash
mvn test
```

Incluye:
- **Pruebas unitarias** (`*ServiceTest`, JUnit 5 + Mockito, sin contexto Spring)
  para las reglas de negocio (validaciones, 404 en get/update/delete).
- **Pruebas de integracion** (`*RepositoryTest`, `@DataJpaTest`) que validan
  contra un motor de base de datos real (H2 en memoria) el guardado, las
  consultas derivadas y la paginacion.

## Arquitectura

```
Controller (@RestController)  -->  Service (@Service)  -->  Repository (JpaRepository)
        |                                |                          |
   HTTP + Swagger                 Validaciones / 404          Hibernate + H2 (archivo)
```

- **Persistencia real**: `spring.datasource.url=jdbc:h2:file:./data/eventifydb`.
  Los datos quedan en el archivo `./data/eventifydb.mv.db` y sobreviven a
  apagar/encender la aplicacion (Escenario 1).
- **Entidades JPA**: `Event` y `Venue` con `@Entity`, `@Table`, `@Id` +
  `@GeneratedValue(strategy = GenerationType.IDENTITY)` y `@Column(nullable = false, length = ...)`.
- **Repositorios**: `EventRepository` / `VenueRepository` extienden `JpaRepository`
  e incluyen la Derived Query `findByNameContainingIgnoreCase(String name)`.
- **Seeder inteligente**: `DataSeederConfig` solo siembra datos si las tablas
  estan vacias, para no duplicar registros en cada reinicio.
- **404 controlado**: `ResourceNotFoundException` + `GlobalExceptionHandler`
  devuelven un JSON claro (`timestamp`, `status`, `error`, `message`) en vez
  de un error generico 500.

## Endpoints (CRUD completo)

| Metodo | Ruta | Descripcion | Exito | Error |
|---|---|---|---|---|
| POST | `/api/events` | Crear evento | 201 | 400 |
| GET | `/api/events` | Listar paginado (`?page=0&size=10&sort=name,asc`) | 200 | - |
| GET | `/api/events/{id}` | Consultar por ID | 200 | 404 |
| GET | `/api/events/search?name=...` | Buscar por nombre (Derived Query) | 200 | - |
| PUT | `/api/events/{id}` | Actualizar (valida existencia) | 200 | 400 / 404 |
| DELETE | `/api/events/{id}` | Eliminar (borrado fisico) | 204 | 404 |

Los mismos endpoints existen para `/api/venues`.

## Mapeo a criterios de aceptacion

| Escenario | Como se cumple |
|---|---|
| 1. Persistencia post-reinicio | H2 en modo archivo (`./data/eventifydb`); el seeder no duplica datos si ya existen |
| 2. Recurso inexistente (ID 9999) | `getEventById/updateEvent/deleteEvent` lanzan `ResourceNotFoundException` -> `GlobalExceptionHandler` retorna `404` con mensaje claro |
| 3. Paginacion (50 eventos, page=0, size=5) | `GET /api/events?page=0&size=5` usa `Pageable`; el `Page<Event>` retornado trae `content` (5 items), `totalElements`, `totalPages` |
| 4. Eliminacion exitosa | `DELETE /api/events/{id}` borra fisicamente el registro y retorna `204 No Content` |

## Ejemplos de peticiones

**Crear evento**
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{"name":"Feria Tech","date":"2026-12-01","description":"Feria de tecnologia"}'
```

**Listar paginado y ordenado**
```bash
curl "http://localhost:8080/api/events?page=0&size=5&sort=name,asc"
```

**Consultar por ID**
```bash
curl http://localhost:8080/api/events/1
```

**Actualizar (PUT)**
```bash
curl -X PUT http://localhost:8080/api/events/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Feria Tech 2026","date":"2026-12-05","description":"Fecha actualizada"}'
```

**Eliminar**
```bash
curl -i -X DELETE http://localhost:8080/api/events/1
```

**Buscar por nombre**
```bash
curl "http://localhost:8080/api/events/search?name=tech"
```

**Consultar un ID inexistente (404)**
```bash
curl -i http://localhost:8080/api/events/9999
```

## Probar el Escenario 3 (paginacion con volumen) y 1 (persistencia)

1. Levantar la app: `mvn spring-boot:run`
2. Crear 50 eventos (o usar el `/api/events/search`, `POST` en loop).
3. Consultar `GET /api/events?page=0&size=5` y verificar `content.length == 5`,
   `totalElements == 50`, `totalPages == 10`.
4. Detener la app (`Ctrl+C`) y volver a ejecutar `mvn spring-boot:run`.
5. Consultar `GET /api/events?page=0&size=5` de nuevo: los mismos datos siguen ahi.

## Probar el Escenario 3 original (seeder desactivado / catalogo vacio)

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--eventify.seeder.enabled=false
```
