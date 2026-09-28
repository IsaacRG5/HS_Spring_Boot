package com.eventify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventify.model.Event;

/**
 * Acceso a datos de Event usando Spring Data JPA / Hibernate.
 * Estereotipo @Repository, persistencia real en base de datos.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Consulta derivada (Derived Query): busca eventos cuyo nombre
     * contenga el texto indicado, sin importar mayusculas/minusculas.
     */
    List<Event> findByNameContainingIgnoreCase(String name);

}
