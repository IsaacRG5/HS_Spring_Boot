package com.eventify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventify.model.Venue;

/**
 * Acceso a datos de Venue usando Spring Data JPA / Hibernate.
 * Estereotipo @Repository, persistencia real en base de datos.
 */
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    /**
     * Consulta derivada (Derived Query): busca venues cuyo nombre
     * contenga el texto indicado, sin importar mayusculas/minusculas.
     */
    List<Venue> findByNameContainingIgnoreCase(String name);

}
