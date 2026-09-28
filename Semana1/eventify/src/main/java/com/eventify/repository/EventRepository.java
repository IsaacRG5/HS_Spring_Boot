package com.eventify.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.eventify.model.Event;

/**
 * Simula la base de datos de Events usando una coleccion en memoria.
 * Estereotipo @Repository: capa de acceso a datos.
 */
@Repository
public class EventRepository {

    private final Map<Long, Event> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(idGenerator.getAndIncrement());
        }
        storage.put(event.getId(), event);
        return event;
    }

    public List<Event> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void deleteAll() {
        storage.clear();
    }

}
