package com.eventify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import com.eventify.service.exception.InvalidDataException;

/**
 * Capa de negocio de Venues. Valida la informacion antes de delegar
 * el almacenamiento al @Repository. Estereotipo @Service.
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

    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
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
