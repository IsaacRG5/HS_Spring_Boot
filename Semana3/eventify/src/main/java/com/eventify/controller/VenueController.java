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

import com.eventify.model.Venue;
import com.eventify.service.VenueService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expone la API de Venues: CRUD completo, busqueda y listado paginado.
 * Estereotipo @RestController.
 */
@RestController
@RequestMapping("/api/venues")
@Tag(name = "Venues", description = "Ciclo de vida completo (CRUD) de lugares (venues) del catalogo Eventify")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    @Operation(
            summary = "Registrar un nuevo venue",
            description = "Valida la informacion en la capa Service y la persiste en la base de datos."
    )
    @ApiResponse(responseCode = "201", description = "Venue creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos (ej. nombre vacio o capacidad <= 0)")
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        Venue created = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(
            summary = "Listar venues de forma paginada",
            description = "Soporta paginacion y ordenamiento, ej: ?page=0&size=10&sort=name,asc. "
                    + "Retorna el contenido de la pagina junto con los metadatos (totalPages, totalElements, etc.)."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<Page<Venue>> getAllVenues(
            @ParameterObject @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(venueService.getAllVenues(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar un venue por ID")
    @ApiResponse(responseCode = "200", description = "Venue encontrado")
    @ApiResponse(responseCode = "404", description = "No existe un venue con ese ID")
    public ResponseEntity<Venue> getVenueById(
            @Parameter(description = "ID del venue") @PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Buscar venues por nombre",
            description = "Consulta derivada (findByNameContainingIgnoreCase) sobre el repositorio JPA."
    )
    @ApiResponse(responseCode = "200", description = "Consulta exitosa")
    public ResponseEntity<List<Venue>> searchVenues(
            @Parameter(description = "Texto a buscar dentro del nombre del venue") @RequestParam String name) {
        return ResponseEntity.ok(venueService.searchByName(name));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar un venue existente",
            description = "Valida que el venue exista antes de actualizarlo; si no existe retorna 404."
    )
    @ApiResponse(responseCode = "200", description = "Venue actualizado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos invalidos")
    @ApiResponse(responseCode = "404", description = "No existe un venue con ese ID")
    public ResponseEntity<Venue> updateVenue(
            @Parameter(description = "ID del venue") @PathVariable Long id,
            @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.updateVenue(id, venue));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar un venue",
            description = "Borrado fisico del registro. Si el ID no existe, retorna 404."
    )
    @ApiResponse(responseCode = "204", description = "Venue eliminado exitosamente, sin contenido de respuesta")
    @ApiResponse(responseCode = "404", description = "No existe un venue con ese ID")
    public ResponseEntity<Void> deleteVenue(
            @Parameter(description = "ID del venue") @PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }

}
