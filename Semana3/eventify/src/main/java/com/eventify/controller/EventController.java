package com.eventify.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.model.Event;
import com.eventify.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expone la API de Events: CRUD completo, busqueda y listado paginado.
 * Estereotipo @RestController.
 */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Ciclo de vida completo (CRUD) de eventos del catalogo Eventify")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo evento",
            description = "Valida la informacion en la capa Service y la persiste en la base de datos."
    )
    @ApiResponse(responseCode = "201", description = "Evento creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (ej. nombre vacio)")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Listar eventos de forma paginada",
            description = "Soporta paginacion y ordenamiento, ej: ?page=0&size=10&sort=name,asc. "
                    + "Retorna el contenido de la pagina junto con los metadatos (totalPages, totalElements, etc.)."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<Page<Event>> getAllEvents(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(eventService.getAllEvents(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un evento por ID")
    @ApiResponse(responseCode = "200", description = "Evento encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un evento con ese ID")
    public ResponseEntity<Event> getEventById(
            @Parameter(description = "ID del evento") @PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Buscar eventos por nombre",
            description = "Consulta derivada (findByNameContainingIgnoreCase) sobre el repositorio JPA."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<List<Event>> searchEvents(
            @Parameter(description = "Texto a buscar dentro del nombre del evento") @RequestParam String name) {
        return ResponseEntity.ok(eventService.searchByName(name));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un evento existente",
            description = "Valida que el evento exista antes de actualizarlo; si no existe retorna 404."
    )
    @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "404", description = "No existe un evento con ese ID")
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID del evento") @PathVariable Long id,
            @RequestBody Event event) {
        return ResponseEntity.ok(eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un evento",
            description = "Borrado fisico del registro. Si el ID no existe, retorna 404."
    )
    @ApiResponse(responseCode = "204", description = "Evento eliminado exitosamente, sin contenido de respuesta")
    @ApiResponse(responseCode = "404", description = "No existe un evento con ese ID")
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID del evento") @PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

}
