package com.eventify.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que representa un evento del catalogo de Eventify.
 * Mapeada a la tabla "events" con restricciones de columna que
 * aseguran la integridad desde la base de datos.
 */
@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    // Se evita el nombre de columna "date" (palabra reservada en varios motores de BD)
    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @Column(name = "description", length = 500)
    private String description;

}
