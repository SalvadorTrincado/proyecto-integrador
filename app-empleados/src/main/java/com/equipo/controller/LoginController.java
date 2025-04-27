package com.autenticacion.controller;

import com.autenticacion.model.LoginPaso1;
import com.autenticacion.model.LoginPaso2;
import com.autenticacion.model.LoginPaso3;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")
@SessionAttributes({"loginPaso1", "loginPaso2", "loginPaso3"})
public class LoginController {

    @ModelAttribute("loginPaso1")
    public LoginPaso1 loginPaso1() {
        return new LoginPaso1();
    }

    @ModelAttribute("loginPaso2")
    public LoginPaso2 loginPaso2() {
        return new LoginPaso2();
    }

    @ModelAttribute("loginPaso3")
    public LoginPaso3 loginPaso3() {
        return new LoginPaso3();
    }

    @GetMapping("/paso1")
    public String mostrarPaso1(Model model) {
        return "paso1";
    }

    @PostMapping("/paso1")
    public String procesarPaso1(@Valid @ModelAttribute LoginPaso1 loginPaso1,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "paso1";
        }
        return "redirect:/login/paso2";
    }

    @GetMapping("/paso2")
    public String mostrarPaso2(Model model) {
        return "paso2";
    }

    @PostMapping("/paso2")
    public String procesarPaso2(@Valid @ModelAttribute LoginPaso2 loginPaso2,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "paso2";
        }
        return "redirect:/login/paso3";
    }

    @GetMapping("/paso3")
    public String mostrarPaso3(Model model) {
        return "paso3";
    }

    @PostMapping("/paso3")
    public String procesarPaso3(@Valid @ModelAttribute LoginPaso3 loginPaso3,
                                BindingResult result) {
        if (result.hasErrors()) {
            return "paso3";
        }
        return "redirect:/login/resumen";
    }

    @GetMapping("/resumen")
    public String mostrarResumen(Model model) {
        return "resumen";
    }
}
