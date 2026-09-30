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
import java.util.Optional;

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
import com.eventify.service.exception.ResourceNotFoundException;

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

    @Test
    @DisplayName("Consulta por ID: retorna el venue cuando existe")
    void shouldReturnVenueWhenIdExists() {
        Venue existing = Venue.builder().id(1L).name("Venue X").address("Dir").capacity(100).build();
        when(venueRepository.findById(1L)).thenReturn(Optional.of(existing));

        Venue result = venueService.getVenueById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al consultar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnGet() {
        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.getVenueById(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Actualiza un venue existente con datos validos")
    void shouldUpdateVenueWhenExistsAndDataIsValid() {
        Venue existing = Venue.builder().id(1L).name("Nombre viejo").address("Dir vieja").capacity(50).build();
        Venue newData = Venue.builder().name("Nombre nuevo").address("Dir nueva").capacity(200).build();

        when(venueRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(venueRepository.save(any(Venue.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Venue result = venueService.updateVenue(1L, newData);

        assertThat(result.getName()).isEqualTo("Nombre nuevo");
        assertThat(result.getCapacity()).isEqualTo(200);
        verify(venueRepository, times(1)).save(existing);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al actualizar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnUpdate() {
        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());
        Venue newData = Venue.builder().name("X").address("Y").capacity(10).build();

        assertThatThrownBy(() -> venueService.updateVenue(9999L, newData))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(venueRepository, never()).save(any(Venue.class));
    }

    @Test
    @DisplayName("Escenario 4: elimina un venue existente (borrado fisico)")
    void shouldDeleteVenueWhenExists() {
        Venue existing = Venue.builder().id(1L).name("Venue a borrar").address("Dir").capacity(10).build();
        when(venueRepository.findById(1L)).thenReturn(Optional.of(existing));

        venueService.deleteVenue(1L);

        verify(venueRepository, times(1)).delete(existing);
    }

    @Test
    @DisplayName("Escenario 2 (404): lanza ResourceNotFoundException al eliminar un ID inexistente")
    void shouldThrowNotFoundWhenIdDoesNotExistOnDelete() {
        when(venueRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> venueService.deleteVenue(9999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(venueRepository, never()).delete(any(Venue.class));
    }

}
