package com.equipo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalAreaController {

    @GetMapping("/aplicacion_corporativa/area_personal")
    public String mostrarAreaPersonal() {
        return "aplicacion_corporativa/area_personal";
    }
}