package com.eventify.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventify.model.Event;
import com.eventify.service.EventService;
import com.eventify.service.exception.InvalidDataException;
import com.eventify.service.exception.ResourceNotFoundException;

/**
 * Controller de vista (@Controller) para el listado y registro visual
 * de Events dentro del panel administrativo. Ruta base: /admin/events
 * (separada de la API en /api/events).
 */
@Controller
public class AdminEventViewController {

    private final EventService eventService;

    public AdminEventViewController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/admin/events")
    public String listEvents(Model model) {
        // El objeto Model transporta la lista y un Event vacio para el th:object del formulario
        model.addAttribute("events", eventService.getAllEvents());
        if (!model.containsAttribute("event")) {
            model.addAttribute("event", new Event());
        }
        return "admin/events";
    }

    @PostMapping("/admin/events")
    public String createEvent(@ModelAttribute("event") Event event, RedirectAttributes redirectAttributes) {
        try {
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("successMessage",
                    "El evento \"" + event.getName() + "\" se registro correctamente.");
        } catch (InvalidDataException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        // Patron Post-Redirect-Get: evita el reenvio del formulario al refrescar la pagina
        return "redirect:/admin/events";
    }

    @GetMapping("/admin/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "El evento se elimino correctamente.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/events";
    }

}
