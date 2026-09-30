package com.eventify.web;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;

/**
 * Pruebas de la interfaz (MockMvc) para AdminVenueViewController.
 * Verifican codigo 200, nombre de vista y atributos del Model
 * (Escenario 4 de la HU), ademas de los Escenarios 1, 2 y 3.
 */
@WebMvcTest(AdminVenueViewController.class)
@DisplayName("AdminVenueViewController - pruebas de interfaz (MockMvc)")
class AdminVenueViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VenueService venueService;

    @Test
    @DisplayName("Escenario 1: con lugares registrados, retorna 200, la vista correcta y la tabla con los datos")
    void shouldRenderVenuesTableWhenVenuesExist() throws Exception {
        List<Venue> venues = List.of(
                Venue.builder().id(1L).name("Coliseo Central").address("Cra 50").capacity(1200).build()
        );
        when(venueService.getAllVenues()).thenReturn(venues);

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venues"))
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attribute("venues", hasSize(1)))
                .andExpect(content().string(Matchers.containsString("Coliseo Central")));
    }

    @Test
    @DisplayName("Escenario 2: catalogo vacio muestra el mensaje amigable y no una tabla vacia")
    void shouldShowFriendlyMessageWhenNoVenues() throws Exception {
        when(venueService.getAllVenues()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/venues"))
                .andExpect(model().attribute("venues", hasSize(0)))
                .andExpect(content().string(Matchers.containsString("Actualmente no hay lugares registrados")));
    }

    @Test
    @DisplayName("Escenario 4: el Model contiene la lista de venues necesaria para la vista")
    void modelShouldContainVenuesAttribute() throws Exception {
        when(venueService.getAllVenues()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/venues"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("venues"))
                .andExpect(model().attributeExists("venue"));
    }

    @Test
    @DisplayName("Escenario 3: registrar un lugar valido guarda el dato y redirige al listado (Post-Redirect-Get)")
    void shouldSaveVenueAndRedirectToListing() throws Exception {
        when(venueService.createVenue(any(Venue.class)))
                .thenReturn(Venue.builder().id(1L).name("Teatro Municipal").address("Calle 10").capacity(500).build());

        mockMvc.perform(post("/admin/venues")
                        .param("name", "Teatro Municipal")
                        .param("address", "Calle 10")
                        .param("capacity", "500"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/venues"));

        verify(venueService, times(1)).createVenue(any(Venue.class));
    }

}
