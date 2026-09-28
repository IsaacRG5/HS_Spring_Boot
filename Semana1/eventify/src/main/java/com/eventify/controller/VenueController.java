package com.eventify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expone la API de Venues. Estereotipo @RestController.
 */
@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Registro y consulta de lugares (venues) del catalogo Eventify")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo venue",
            description = "Valida la informacion en la capa Service y la almacena en el repositorio en memoria."
    )
    @ApiResponse(responseCode = "201", description = "Venue creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (ej. nombre vacio o capacidad <= 0)")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue created = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Listar todos los venues",
            description = "Retorna el catalogo completo de venues registrados (puede ser una lista vacia)."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

}
