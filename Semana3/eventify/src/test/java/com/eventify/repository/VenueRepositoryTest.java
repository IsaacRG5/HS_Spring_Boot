package com.eventify.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.eventify.model.Venue;

/**
 * Pruebas de integracion con @DataJpaTest para VenueRepository, sobre
 * una base de datos H2 en memoria embebida por Spring Boot.
 */
@DataJpaTest
@DisplayName("VenueRepository - pruebas de integracion (JPA)")
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Guarda un venue y le asigna un ID autogenerado")
    void shouldPersistVenueAndGenerateId() {
        Venue venue = Venue.builder()
                .name("Teatro Amira de la Rosa")
                .address("Paseo Bolivar, Barranquilla")
                .capacity(800)
                .build();

        Venue saved = venueRepository.save(venue);

        assertThat(saved.getId()).isNotNull();
        assertThat(venueRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase encuentra coincidencias parciales sin importar mayusculas")
    void shouldFindVenuesByNameContaining() {
        venueRepository.save(Venue.builder().name("Coliseo Central").address("Cra 1").capacity(1000).build());
        venueRepository.save(Venue.builder().name("Teatro Municipal").address("Cra 2").capacity(300).build());
        venueRepository.save(Venue.builder().name("Coliseo del Norte").address("Cra 3").capacity(1500).build());

        List<Venue> results = venueRepository.findByNameContainingIgnoreCase("coliseo");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Venue::getName)
                .containsExactlyInAnyOrder("Coliseo Central", "Coliseo del Norte");
    }

    @Test
    @DisplayName("La paginacion retorna el tamano solicitado y los metadatos correctos")
    void shouldReturnPagedResultsWithMetadata() {
        for (int i = 1; i <= 20; i++) {
            venueRepository.save(Venue.builder()
                    .name(String.format("Venue %02d", i))
                    .address("Direccion " + i)
                    .capacity(100 + i)
                    .build());
        }

        Page<Venue> page = venueRepository.findAll(PageRequest.of(1, 5, Sort.by("name").ascending()));

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalElements()).isEqualTo(20);
        assertThat(page.getTotalPages()).isEqualTo(4);
        assertThat(page.getNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("El borrado fisico elimina el registro de la base de datos")
    void shouldDeleteVenuePhysically() {
        Venue saved = venueRepository.save(Venue.builder().name("Venue a borrar").address("Dir").capacity(50).build());

        venueRepository.delete(saved);

        assertThat(venueRepository.findById(saved.getId())).isEmpty();
    }

}
