package com.eventify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import com.eventify.service.exception.InvalidDataException;

/**
 * Capa de negocio de Events. Valida la informacion antes de delegar
 * el almacenamiento al @Repository. Estereotipo @Service.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    // Inyeccion por constructor (obligatoria, facilita el testeo con Mockito)
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event createEvent(Event event) {
        validate(event);
        // Se fuerza un id nuevo para evitar que el cliente sobreescriba un registro existente
        event.setId(null);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    private void validate(Event event) {
        if (event == null) {
            throw new InvalidDataException("El evento no puede ser nulo");
        }
        if (event.getName() == null || event.getName().isBlank()) {
            throw new InvalidDataException("El nombre del evento no puede estar vacio");
        }
        if (event.getDate() == null) {
            throw new InvalidDataException("La fecha del evento es obligatoria");
        }
    }

}
