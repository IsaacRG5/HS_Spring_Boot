package com.eventify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eventify.model.Event;
import com.eventify.repository.EventRepository;
import com.eventify.service.exception.InvalidDataException;

/**
 * Pruebas unitarias de EventService de forma aislada, mockeando el
 * EventRepository. No se levanta el contexto de Spring (@ExtendWith
 * MockitoExtension en lugar de @SpringBootTest).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventService - pruebas unitarias")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event validEvent;

    @BeforeEach
    void setUp() {
        validEvent = Event.builder()
                .name("Concierto de Rock")
                .date(LocalDate.now().plusDays(10))
                .description("Concierto al aire libre")
                .build();
    }

    @Test
    @DisplayName("Escenario 1: registra un evento valido y retorna el objeto creado")
    void shouldCreateEventWhenDataIsValid() {
        Event saved = Event.builder().id(1L).name(validEvent.getName())
                .date(validEvent.getDate()).description(validEvent.getDescription()).build();
        when(eventRepository.save(any(Event.class))).thenReturn(saved);

        Event result = eventService.createEvent(validEvent);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Concierto de Rock");
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Escenario 2: rechaza un evento con nombre vacio y no llega al repositorio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        Event invalidEvent = Event.builder()
                .name("")
                .date(LocalDate.now())
                .description("desc")
                .build();

        assertThatThrownBy(() -> eventService.createEvent(invalidEvent))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("nombre");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Rechaza un evento con nombre nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        Event invalidEvent = Event.builder().name(null).date(LocalDate.now()).build();

        assertThatThrownBy(() -> eventService.createEvent(invalidEvent))
                .isInstanceOf(InvalidDataException.class);

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Rechaza un evento sin fecha")
    void shouldThrowExceptionWhenDateIsNull() {
        Event invalidEvent = Event.builder().name("Evento sin fecha").date(null).build();

        assertThatThrownBy(() -> eventService.createEvent(invalidEvent))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("fecha");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Escenario 3: retorna lista vacia cuando no hay eventos cargados")
    void shouldReturnEmptyListWhenNoEventsRegistered() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getAllEvents();

        assertThat(result).isNotNull().isEmpty();
        verify(eventRepository, times(1)).findAll();
    }

}
