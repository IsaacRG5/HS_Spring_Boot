package com.eventify.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import com.eventify.service.exception.InvalidDataException;
import com.eventify.service.exception.ResourceNotFoundException;

/**
 * Capa de negocio de Events. Valida la informacion, orquesta el ciclo
 * de vida completo (CRUD) y delega la persistencia real al @Repository
 * (Spring Data JPA / Hibernate). Estereotipo @Service.
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
        event.setId(null);
        return eventRepository.save(event);
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontro el evento con id " + id));
    }

    public Event updateEvent(Long id, Event updatedData) {
        Event existing = getEventById(id);
        validate(updatedData);

        existing.setName(updatedData.getName());
        existing.setDate(updatedData.getDate());
        existing.setDescription(updatedData.getDescription());

        return eventRepository.save(existing);
    }

    public void deleteEvent(Long id) {
        Event existing = getEventById(id);
        eventRepository.delete(existing);
    }

    public List<Event> searchByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name);
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
