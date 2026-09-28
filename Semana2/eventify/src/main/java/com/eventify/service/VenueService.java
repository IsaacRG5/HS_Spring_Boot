package com.eventify.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import com.eventify.service.exception.InvalidDataException;
import com.eventify.service.exception.ResourceNotFoundException;

/**
 * Capa de negocio de Venues. Valida la informacion, orquesta el ciclo
 * de vida completo (CRUD) y delega la persistencia real al @Repository
 * (Spring Data JPA / Hibernate). Estereotipo @Service.
 */
@Service
public class VenueService {

    private final VenueRepository venueRepository;

    // Inyeccion por constructor (obligatoria, facilita el testeo con Mockito)
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public Venue createVenue(Venue venue) {
        validate(venue);
        venue.setId(null);
        return venueRepository.save(venue);
    }

    public Page<Venue> getAllVenues(Pageable pageable) {
        return venueRepository.findAll(pageable);
    }

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el venue con id " + id));
    }

    public Venue updateVenue(Long id, Venue updatedData) {
        Venue existing = getVenueById(id);
        validate(updatedData);

        existing.setName(updatedData.getName());
        existing.setAddress(updatedData.getAddress());
        existing.setCapacity(updatedData.getCapacity());

        return venueRepository.save(existing);
    }

    public void deleteVenue(Long id) {
        Venue existing = getVenueById(id);
        venueRepository.delete(existing);
    }

    public List<Venue> searchByName(String name) {
        return venueRepository.findByNameContainingIgnoreCase(name);
    }

    private void validate(Venue venue) {
        if (venue == null) {
            throw new InvalidDataException("El venue no puede ser nulo");
        }
        if (venue.getName() == null || venue.getName().isBlank()) {
            throw new InvalidDataException("El nombre del venue no puede estar vacio");
        }
        if (venue.getAddress() == null || venue.getAddress().isBlank()) {
            throw new InvalidDataException("La direccion del venue es obligatoria");
        }
        if (venue.getCapacity() <= 0) {
            throw new InvalidDataException("La capacidad del venue debe ser mayor a cero");
        }
    }

}
