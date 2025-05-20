package com.equipo.controller;

import com.equipo.dto.registroPasos.DatosContactoDTO;
import com.equipo.dto.registroPasos.DatosEconomicosDTO;
import com.equipo.dto.registroPasos.DatosPersonalesDTO;
import com.equipo.dto.registroPasos.DatosProfesionalesDTO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/aplicacion_corporativa/registro")
public class RegistroPasosController {

    @GetMapping("/paso1")
    public String mostrarPaso1(Model model) {
        if (!model.containsAttribute("datosPersonales")) {
            model.addAttribute("datosPersonales", new DatosPersonalesDTO());
        }
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/paso1";
    }

    @PostMapping("/paso1")
    public String procesarPaso1(@ModelAttribute("datosPersonales") @Valid DatosPersonalesDTO datosPersonales,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro/paso1";
        }
        return "redirect:/aplicacion_corporativa/registro/paso2";
    }

    @GetMapping("/paso2")
    public String mostrarPaso2(Model model) {
        if (!model.containsAttribute("datosContacto")) {
            model.addAttribute("datosContacto", new DatosContactoDTO());
        }
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/paso2";
    }

    @PostMapping("/paso2")
    public String procesarPaso2(@ModelAttribute("datosContacto") @Valid DatosContactoDTO datosContacto,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro/paso2";
        }
        return "redirect:/aplicacion_corporativa/registro/paso3";
    }

    @GetMapping("/paso3")
    public String mostrarPaso3(Model model) {
        if (!model.containsAttribute("datosProfesionales")) {
            model.addAttribute("datosProfesionales", new DatosProfesionalesDTO());
        }
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/paso3";
    }

    @PostMapping("/paso3")
    public String procesarPaso3(@ModelAttribute("datosProfesionales") @Valid DatosProfesionalesDTO datosProfesionales,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro/paso3";
        }
        return "redirect:/aplicacion_corporativa/registro/paso4";
    }

    @GetMapping("/paso4")
    public String mostrarPaso4(Model model) {
        if (!model.containsAttribute("datosEconomicos")) {
            model.addAttribute("datosEconomicos", new DatosEconomicosDTO());
        }
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/paso4";
    }

    @PostMapping("/paso4")
    public String procesarPaso4(@ModelAttribute("datosEconomicos") @Valid DatosEconomicosDTO datosEconomicos,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro/paso4";
        }
        return "redirect:/aplicacion_corporativa/registro/resumen";
    }

    @GetMapping("/resumen")
    public String mostrarResumen() {
        return "aplicacion_corporativa/registro/resumen";
    }
}

