package com.eventify.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eventify.service.EventService;
import com.eventify.service.VenueService;

/**
 * Controller de vista (@Controller, no @RestController) del panel
 * administrativo. Retorna nombres de plantillas HTML (Thymeleaf) en
 * lugar de JSON. Ruta base: /admin (separada de /api).
 */
@Controller
public class AdminDashboardController {

    private final EventService eventService;
    private final VenueService venueService;

    public AdminDashboardController(EventService eventService, VenueService venueService) {
        this.eventService = eventService;
        this.venueService = venueService;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("totalVenues", venueService.getAllVenues().size());
        return "admin/index";
    }

}
