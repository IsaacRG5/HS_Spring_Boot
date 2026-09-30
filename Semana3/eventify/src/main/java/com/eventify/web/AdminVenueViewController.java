package com.eventify.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventify.model.Venue;
import com.eventify.service.VenueService;
import com.eventify.service.exception.InvalidDataException;
import com.eventify.service.exception.ResourceNotFoundException;

/**
 * Controller de vista (@Controller) para el listado y registro visual
 * de Venues dentro del panel administrativo. Ruta base: /admin/venues
 * (separada de la API en /api/venues).
 */
@Controller
public class AdminVenueViewController {

    private final VenueService venueService;

    public AdminVenueViewController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping("/admin/venues")
    public String listVenues(Model model) {
        model.addAttribute("venues", venueService.getAllVenues());
        if (!model.containsAttribute("venue")) {
            model.addAttribute("venue", new Venue());
        }
        return "admin/venues";
    }

    @PostMapping("/admin/venues")
    public String createVenue(@ModelAttribute("venue") Venue venue, RedirectAttributes redirectAttributes) {
        try {
            venueService.createVenue(venue);
            redirectAttributes.addFlashAttribute("successMessage",
                    "El lugar \"" + venue.getName() + "\" se registro correctamente.");
        } catch (InvalidDataException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        // Patron Post-Redirect-Get: evita el reenvio del formulario al refrescar la pagina
        return "redirect:/admin/venues";
    }

    @GetMapping("/admin/venues/delete/{id}")
    public String deleteVenue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            venueService.deleteVenue(id);
            redirectAttributes.addFlashAttribute("successMessage", "El lugar se elimino correctamente.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/venues";
    }

}
