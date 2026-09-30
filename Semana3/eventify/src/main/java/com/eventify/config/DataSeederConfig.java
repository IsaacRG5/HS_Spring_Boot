package com.eventify.config;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eventify.model.Event;
import com.eventify.model.Venue;
import com.eventify.repository.EventRepository;
import com.eventify.repository.VenueRepository;

/**
 * Carga datos iniciales (Seeders) en el arranque de la aplicacion.
 * Controlado por la propiedad "eventify.seeder.enabled" (application.properties),
 * lo que permite reproducir el Escenario 3 (catalogo vacio) cuando esta en false.
 *
 * Como la persistencia ahora es real (Spring Data JPA + H2 en archivo), se
 * valida que las tablas esten vacias antes de sembrar, para no duplicar
 * registros en cada reinicio de la aplicacion (Escenario 1: persistencia
 * post-reinicio).
 */
@Configuration
public class DataSeederConfig {

    @Bean
    CommandLineRunner seedInitialData(
            EventRepository eventRepository,
            VenueRepository venueRepository,
            @Value("${eventify.seeder.enabled:true}") boolean seederEnabled) {

        return args -> {
            if (!seederEnabled) {
                return;
            }
            if (eventRepository.count() > 0 || venueRepository.count() > 0) {
                // Ya hay datos persistidos de un arranque anterior: no se vuelve a sembrar
                return;
            }

            Venue mainVenue = venueRepository.save(
                    Venue.builder()
                            .name("Centro de Convenciones Eventify")
                            .address("Calle 72 # 10-34, Barranquilla")
                            .capacity(500)
                            .build()
            );

            eventRepository.save(
                    Event.builder()
                            .name("Lanzamiento Eventify")
                            .date(LocalDate.now().plusDays(30))
                            .description("Evento de lanzamiento oficial de la plataforma, en " + mainVenue.getName())
                            .build()
            );
        };
    }

}
