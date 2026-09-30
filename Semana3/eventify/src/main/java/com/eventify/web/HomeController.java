package com.eventify.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirige la raiz del sitio hacia el panel administrativo, para que
 * los coordinadores no tengan que recordar la ruta "/admin" de memoria.
 * Vive en el paquete "web" (rutas de interfaz), separado del paquete
 * "controller" (rutas de API), reforzando la separacion API/vistas.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/admin";
    }

}
