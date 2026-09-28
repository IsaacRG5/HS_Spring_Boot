package com.eventify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.model.Event;
import com.eventify.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expone la API de Events. Estereotipo @RestController.
 */
@RestController
@RequestMapping("/api/events")
@Tag(name = "Events", description = "Registro y consulta de eventos del catalogo Eventify")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo evento",
            description = "Valida la informacion en la capa Service y la almacena en el repositorio en memoria."
    )
    @ApiResponse(responseCode = "201", description = "Evento creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (ej. nombre vacio)")
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event created = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los eventos",
            description = "Retorna el catalogo completo de eventos registrados (puede ser una lista vacia)."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

}
