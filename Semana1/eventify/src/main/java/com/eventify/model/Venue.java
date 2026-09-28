package com.eventify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un lugar (venue) del catalogo de Eventify.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    private Long id;
    private String name;
    private String address;
    private int capacity;

}
