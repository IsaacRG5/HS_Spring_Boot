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
import java.util.Optional;

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
import com.eventify.service.exception.ResourceNotFoundException;

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

    @Test
    @DisplayName("Consulta por ID: retorna el evento cuando existe")
    void shouldReturnEventWhenIdExists() {
        Event existing = Event.builder().id(1L).name("Evento X").date(LocalDate.now()).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));

        Event result = eventService.getEventById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al consultar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnGet() {
        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventById(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Actualiza un evento existente con datos validos")
    void shouldUpdateEventWhenExistsAndDataIsValid() {
        Event existing = Event.builder().id(1L).name("Nombre viejo").date(LocalDate.now()).build();
        Event newData = Event.builder().name("Nombre nuevo").date(LocalDate.now().plusDays(1))
                .description("desc nueva").build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.updateEvent(1L, newData);

        assertThat(result.getName()).isEqualTo("Nombre nuevo");
        assertThat(result.getDescription()).isEqualTo("desc nueva");
        verify(eventRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al actualizar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnUpdate() {
        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());
        Event newData = Event.builder().name("X").date(LocalDate.now()).build();

        assertThatThrownBy(() -> eventService.updateEvent(9999L, newData))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Escenario 4: elimina un evento existente (borrado fisico)")
    void shouldDeleteEventWhenExists() {
        Event existing = Event.builder().id(1L).name("Evento a borrar").date(LocalDate.now()).build();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));

        eventService.deleteEvent(1L);

        verify(eventRepository, times(1)).delete(existing);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al eliminar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnDelete() {
        when(eventRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.deleteEvent(9999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(eventRepository, never()).delete(any(Event.class));
    }

}
