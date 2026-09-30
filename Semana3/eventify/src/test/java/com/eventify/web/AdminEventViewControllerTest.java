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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eventify.model.Event;
import com.eventify.service.EventService;

/**
 * Pruebas de la interfaz (MockMvc) para AdminEventViewController.
 * Verifican codigo 200, nombre de vista y atributos del Model
 * (Escenario 4 de la HU), ademas de los Escenarios 1, 2 y 3.
 */
@WebMvcTest(AdminEventViewController.class)
@DisplayName("AdminEventViewController - pruebas de interfaz (MockMvc)")
class AdminEventViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    @DisplayName("Escenario 1: con eventos registrados, retorna 200, la vista correcta y la tabla con los datos")
    void shouldRenderEventsTableWhenEventsExist() throws Exception {
        List<Event> events = List.of(
                Event.builder().id(1L).name("Feria Tech").date(LocalDate.of(2026, 12, 1)).description("Feria anual").build()
        );
        when(eventService.getAllEvents()).thenReturn(events);

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(content().string(Matchers.containsString("Feria Tech")));
    }

    @Test
    @DisplayName("Escenario 2: catalogo vacio muestra el mensaje amigable y no una tabla vacia")
    void shouldShowFriendlyMessageWhenNoEvents() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/events"))
                .andExpect(model().attribute("events", hasSize(0)))
                .andExpect(content().string(Matchers.containsString("Actualmente no hay eventos programados")));
    }

    @Test
    @DisplayName("Escenario 4: el Model contiene la lista de eventos necesaria para la vista")
    void modelShouldContainEventsAttribute() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/events"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("events"))
                .andExpect(model().attributeExists("event"));
    }

    @Test
    @DisplayName("Escenario 3: registrar un evento valido guarda el dato y redirige al listado (Post-Redirect-Get)")
    void shouldSaveEventAndRedirectToListing() throws Exception {
        when(eventService.createEvent(any(Event.class)))
                .thenReturn(Event.builder().id(1L).name("Lanzamiento Eventify").date(LocalDate.now()).build());

        mockMvc.perform(post("/admin/events")
                        .param("name", "Lanzamiento Eventify")
                        .param("date", "2026-12-01")
                        .param("description", "Evento de lanzamiento"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/events"));

        verify(eventService, times(1)).createEvent(any(Event.class));
    }

}
