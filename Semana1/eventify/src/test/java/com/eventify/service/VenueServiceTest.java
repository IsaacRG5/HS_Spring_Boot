package com.eventify.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eventify.model.Venue;
import com.eventify.repository.VenueRepository;
import com.eventify.service.exception.InvalidDataException;

/**
 * Pruebas unitarias de VenueService de forma aislada, mockeando el
 * VenueRepository. No se levanta el contexto de Spring.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("VenueService - pruebas unitarias")
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    private Venue validVenue;

    @BeforeEach
    void setUp() {
        validVenue = Venue.builder()
                .name("Teatro Amira de la Rosa")
                .address("Paseo Bolivar, Barranquilla")
                .capacity(800)
                .build();
    }

    @Test
    @DisplayName("Registra un venue valido y retorna el objeto creado")
    void shouldCreateVenueWhenDataIsValid() {
        Venue saved = Venue.builder().id(1L).name(validVenue.getName())
                .address(validVenue.getAddress()).capacity(validVenue.getCapacity()).build();
        when(venueRepository.save(any(Venue.class))).thenReturn(saved);

        Venue result = venueService.createVenue(validVenue);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(venueRepository, times(1)).save(any(Venue.class));
    }

    @Test
    @DisplayName("Rechaza un venue con nombre vacio y no llega al repositorio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        Venue invalidVenue = Venue.builder().name(" ").address("Direccion valida").capacity(100).build();

        assertThatThrownBy(() -> venueService.createVenue(invalidVenue))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("nombre");

        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    @DisplayName("Rechaza un venue con capacidad menor o igual a cero")
    void shouldThrowExceptionWhenCapacityIsInvalid() {
        Venue invalidVenue = Venue.builder().name("Auditorio").address("Calle 45").capacity(0).build();

        assertThatThrownBy(() -> venueService.createVenue(invalidVenue))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("capacidad");

        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    @DisplayName("Retorna lista vacia cuando no hay venues cargados (seeder desactivado)")
    void shouldReturnEmptyListWhenNoVenuesRegistered() {
        when(venueRepository.findAll()).thenReturn(Collections.emptyList());

        List<Venue> result = venueService.getAllVenues();

        assertThat(result).isNotNull().isEmpty();
        verify(venueRepository, times(1)).findAll();
    }

}
