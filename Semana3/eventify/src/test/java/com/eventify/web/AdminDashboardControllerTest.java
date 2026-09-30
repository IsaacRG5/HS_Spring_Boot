package com.eventify.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import com.eventify.service.VenueService;

/**
 * Pruebas de la interfaz (MockMvc) para AdminDashboardController.
 * Verifica que la ruta del panel retorne 200, la vista correcta y
 * que el Model contenga los atributos que la vista necesita.
 */
@WebMvcTest(AdminDashboardController.class)
@DisplayName("AdminDashboardController - pruebas de interfaz (MockMvc)")
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private VenueService venueService;

    @Test
    @DisplayName("Escenario 4: GET /admin retorna 200, la vista admin/index y los contadores en el Model")
    void shouldRenderDashboardWithCounters() throws Exception {
        when(eventService.getAllEvents()).thenReturn(List.of(Event.builder().id(1L).build()));
        when(venueService.getAllVenues()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"))
                .andExpect(model().attributeExists("totalEvents"))
                .andExpect(model().attributeExists("totalVenues"))
                .andExpect(model().attribute("totalEvents", 1))
                .andExpect(model().attribute("totalVenues", 0));
    }

}
