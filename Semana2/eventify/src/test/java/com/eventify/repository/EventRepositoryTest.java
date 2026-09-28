package com.eventify.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.eventify.model.Event;

/**
 * Pruebas de integracion con @DataJpaTest: levanta solo la capa de
 * persistencia (con una base H2 en memoria embebida) para validar que
 * las entidades se guardan correctamente y que las consultas derivadas
 * y la paginacion funcionan contra un motor de base de datos real.
 */
@DataJpaTest
@DisplayName("EventRepository - pruebas de integracion (JPA)")
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Guarda un evento y le asigna un ID autogenerado")
    void shouldPersistEventAndGenerateId() {
        Event event = Event.builder()
                .name("Feria de Tecnologia")
                .date(LocalDate.now().plusDays(15))
                .description("Feria anual de innovacion")
                .build();

        Event saved = eventRepository.save(event);

        assertThat(saved.getId()).isNotNull();
        assertThat(eventRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase encuentra coincidencias parciales sin importar mayusculas")
    void shouldFindEventsByNameContaining() {
        eventRepository.save(Event.builder().name("Concierto de Jazz").date(LocalDate.now()).build());
        eventRepository.save(Event.builder().name("Feria Tech").date(LocalDate.now()).build());
        eventRepository.save(Event.builder().name("Festival de Jazz Latino").date(LocalDate.now()).build());

        List<Event> results = eventRepository.findByNameContainingIgnoreCase("jazz");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Event::getName)
                .containsExactlyInAnyOrder("Concierto de Jazz", "Festival de Jazz Latino");
    }

    @Test
    @DisplayName("Escenario 3: la paginacion retorna el tamano solicitado y los metadatos correctos")
    void shouldReturnPagedResultsWithMetadata() {
        for (int i = 1; i <= 50; i++) {
            eventRepository.save(Event.builder()
                    .name(String.format("Evento %02d", i))
                    .date(LocalDate.now().plusDays(i))
                    .build());
        }

        Page<Event> page = eventRepository.findAll(PageRequest.of(0, 5, Sort.by("name").ascending()));

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalElements()).isEqualTo(50);
        assertThat(page.getTotalPages()).isEqualTo(10);
        assertThat(page.getNumber()).isZero();
    }

    @Test
    @DisplayName("El borrado fisico elimina el registro de la base de datos")
    void shouldDeleteEventPhysically() {
        Event saved = eventRepository.save(Event.builder().name("Evento a borrar").date(LocalDate.now()).build());

        eventRepository.delete(saved);

        assertThat(eventRepository.findById(saved.getId())).isEmpty();
    }

}
