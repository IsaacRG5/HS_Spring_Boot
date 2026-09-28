package com.eventify.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.eventify.model.Venue;

/**
 * Simula la base de datos de Venues usando una coleccion en memoria.
 * Estereotipo @Repository: capa de acceso a datos.
 */
@Repository
public class VenueRepository {

    private final Map<Long, Venue> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(idGenerator.getAndIncrement());
        }
        storage.put(venue.getId(), venue);
        return venue;
    }

    public List<Venue> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public void deleteAll() {
        storage.clear();
    }

}
