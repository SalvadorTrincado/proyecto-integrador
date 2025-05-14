package com.equipo.controller;

import com.equipo.dto.RegistroEmpleadoPaso1DTO;
import com.equipo.dto.RegistroEmpleadoPaso2DTO;
import com.equipo.dto.RegistroEmpleadoPaso3DTO;
import com.equipo.dto.RegistroEmpleadoPaso4DTO;
import com.equipo.entity.Empleado;
import com.equipo.service.EmpleadoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class RegistroEmpleadoController {

    private final EmpleadoService empleadoService;

    @Autowired
    public RegistroEmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    // --- Paso 1 ---
    @GetMapping("/registro_empleado_paso1")
    public String mostrarFormularioPaso1(@ModelAttribute("registroEmpleadoPaso1DTO") RegistroEmpleadoPaso1DTO paso1,
                                         Model model,
                                         HttpSession sesion) {

        RegistroEmpleadoPaso1DTO paso1DTO = (RegistroEmpleadoPaso1DTO) sesion.getAttribute("registroEmpleadoPaso1DTO");
        if (paso1DTO != null) {
            model.addAttribute("registroEmpleadoPaso1DTO", paso1DTO);
        }

        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro_empleado_paso1";
    }

    @PostMapping("/registro_empleado_paso1-post")
    public String procesarFormularioPaso1(@Valid @ModelAttribute("registroEmpleadoPaso1DTO") RegistroEmpleadoPaso1DTO paso1,
                                          BindingResult result,
                                          Model model,
                                          HttpSession session) throws IOException {
        if(result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro_empleado_paso1";
        }

        session.setAttribute("registroEmpleadoPaso1DTO", paso1);

        return "redirect:/registro_empleado_paso2"; // Redirigir al paso 2
    }

    // --- Paso 2 ---
    @GetMapping("/registro_empleado_paso2")
    public String mostrarFormularioPaso2(@ModelAttribute("registroEmpleadoPaso2DTO") RegistroEmpleadoPaso2DTO paso2,
                                         Model model,
                                         HttpSession session) {

        RegistroEmpleadoPaso2DTO paso2DTO = (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO");

        if (paso2DTO != null) {
            model.addAttribute("registroEmpleadoPaso2DTO", paso2DTO);
        }

        model.addAttribute("mostrarErrores", false);

        return "aplicacion_corporativa/registro_empleado_paso2";
    }

    @PostMapping("/registro_empleado_paso2-post")
    public String procesarFormularioPaso2(@Valid @ModelAttribute("registroEmpleadoPaso2DTO") RegistroEmpleadoPaso2DTO paso2,
                                          BindingResult result,
                                          Model model,
                                          HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro_empleado_paso2";
        }
        session.setAttribute("registroEmpleadoPaso2DTO", paso2);
        return "redirect:/registro_empleado_paso3"; // Redirigir al paso 3
    }

    // --- Paso 3 ---
    @GetMapping("/registro_empleado_paso3")
    public String mostrarFormularioPaso3(@ModelAttribute("registroEmpleadoPaso3DTO") RegistroEmpleadoPaso3DTO paso3,
                                         Model model, HttpSession session) {

        RegistroEmpleadoPaso3DTO paso3DTO = (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO");

        if (paso3DTO != null) {
            model.addAttribute("registroEmpleadoPaso3DTO", paso3DTO);
        }
        List<String> especialidadesPosibles = List.of("Análisis de datos", "Administración de servidores", "Seguridad informática", "Desarrollo backend", "Desarrollo frontend", "Diseño UI/UX");
        model.addAttribute("especialidadesPosibles", especialidadesPosibles);

        model.addAttribute("mostrarErrores", false);

        return "aplicacion_corporativa/registro_empleado_paso3";
    }

    @PostMapping("/registro_empleado_paso3-post")
    public String procesarFormularioPaso3(@Valid @ModelAttribute("registroEmpleadoPaso3DTO") RegistroEmpleadoPaso3DTO paso3,
                                          BindingResult result,
                                          Model model,
                                          HttpSession session) {
        if (result.hasErrors()) {
            List<String> especialidadesPosibles = List.of("Análisis de datos", "Administración de servidores", "Seguridad informática", "Desarrollo backend", "Desarrollo frontend", "Diseño UI/UX");
            model.addAttribute("especialidadesPosibles", especialidadesPosibles);
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro_empleado_paso3";
        }
        session.setAttribute("registroEmpleadoPaso3DTO", paso3);
        return "redirect:/registro_empleado_paso4"; // Redirigir al paso 4
    }

    // --- Paso 4 ---
    @GetMapping("/registro_empleado_paso4")
    public String mostrarFormularioPaso4(@ModelAttribute("registroEmpleadoPaso4DTO") RegistroEmpleadoPaso4DTO paso4,
                                         Model model, HttpSession session) {

        RegistroEmpleadoPaso4DTO paso4DTO = (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO");

        if (paso4DTO != null) {
            model.addAttribute("registroEmpleadoPaso4DTO", paso4DTO);
        }
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro_empleado_paso4";
    }

    @PostMapping("/registro_empleado_paso4-post")
    public String procesarFormularioPaso4(@Valid @ModelAttribute("registroEmpleadoPaso4DTO") RegistroEmpleadoPaso4DTO paso4DTO,
                                          BindingResult result,
                                          Model model,
                                          HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            return "aplicacion_corporativa/registro_empleado_paso4";
        }

        session.setAttribute("registroEmpleadoPaso4DTO", paso4DTO);

        return "redirect:/resumen/exito";
    }

    @GetMapping("/resumen/exito")
    public String mostrarPaginaExito(HttpSession session,
                                     Model modelo) {

        RegistroEmpleadoPaso1DTO paso1DTO = (RegistroEmpleadoPaso1DTO) session.getAttribute("registroEmpleadoPaso1DTO");
        RegistroEmpleadoPaso2DTO paso2DTO = (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO");
        RegistroEmpleadoPaso3DTO paso3DTO = (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO");
        RegistroEmpleadoPaso4DTO paso4DTO = (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO");

        if(paso1DTO == null) {
            paso1DTO = new RegistroEmpleadoPaso1DTO();
        }

        if(paso2DTO == null) {
            paso2DTO = new RegistroEmpleadoPaso2DTO();
        }

        if(paso3DTO == null) {
            paso3DTO = new RegistroEmpleadoPaso3DTO();
        }

        if(paso4DTO == null) {
            paso4DTO = new RegistroEmpleadoPaso4DTO();
        }

        modelo.addAttribute("registroEmpleadoPaso1DTO", paso1DTO);
        modelo.addAttribute("registroEmpleadoPaso2DTO", paso2DTO);
        modelo.addAttribute("registroEmpleadoPaso3DTO", paso3DTO);
        modelo.addAttribute("registroEmpleadoPaso4DTO", paso4DTO);

        return "aplicacion_corporativa/registro_empleado_paso5";
    }

    @PostMapping("/resumen/exito-post")
    public String mostrarPaginaExitoPost(HttpSession session) {

        Empleado empleado = crearEmpleadoDesdeSesion((RegistroEmpleadoPaso1DTO) session.getAttribute("registroEmpleadoPaso1DTO"),
                (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO"),
                (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO"),
                (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO"));
        empleadoService.guardarEmpleado(empleado);
        session.invalidate();
        return "redirect:/aplicacion_corporativa/area_personal";
    }

    private Empleado crearEmpleadoDesdeSesion(RegistroEmpleadoPaso1DTO paso1DTO,
                                              RegistroEmpleadoPaso2DTO paso2DTO,
                                              RegistroEmpleadoPaso3DTO paso3DTO,
                                              RegistroEmpleadoPaso4DTO paso4DTO) {
        Empleado empleado = new Empleado();
        empleado.setNombre(paso1DTO.getNombre());
        empleado.setApellidos(paso1DTO.getApellidos());
        if (paso1DTO.getFotografia() != null) {
            empleado.setFotografia(paso1DTO.getFotografia().getOriginalFilename()); // Guarda el nombre del archivo
        }
        empleado.setGeneroSeleccionado(paso1DTO.getGeneroSeleccionado());
        empleado.setFechaNacimiento(paso1DTO.getFechaNacimiento());
        empleado.setEdad(paso1DTO.getEdad());
        empleado.setPaisNacimiento(paso1DTO.getPaisNacimiento());
        empleado.setComentarios(paso1DTO.getComentarios());

        empleado.setTipoDocumento(paso2DTO.getTipoDocumento());
        empleado.setDocumento(paso2DTO.getDocumento());
        empleado.setPrefijoTelefono(paso2DTO.getPrefijoTelefono());
        empleado.setTelefonoMovil(paso2DTO.getTelefonoMovil());
        empleado.setTipoViaDireccionPpal(paso2DTO.getTipoViaDireccionPpal());
        empleado.setNombreViaDireccionPpal(paso2DTO.getNombreViaDireccionPpal());
        empleado.setNumeroViaDireccionPpal(paso2DTO.getNumeroViaDireccionPpal());
        empleado.setPortalDireccionPpal(paso2DTO.getPortalDireccionPpal());
        empleado.setPlantaDireccionPpal(paso2DTO.getPlantaDireccionPpal());
        empleado.setPuertaDireccionPpal(paso2DTO.getPuertaDireccionPpal());
        empleado.setLocalidadDireccionPpal(paso2DTO.getLocalidadDireccionPpal());
        empleado.setRegionDireccionPpal(paso2DTO.getRegionDireccionPpal());
        empleado.setCodigoPostalDireccionPpal(paso2DTO.getCodigoPostalDireccionPpal());

        empleado.setDepartamento(paso3DTO.getDepartamento());
        empleado.setEspecialidadesSeleccionadas(paso3DTO.getEspecialidadesSeleccionadas());

        empleado.setNumeroCuenta(paso4DTO.getNumeroCuenta());
        empleado.setTipoContrato(paso4DTO.getTipoContrato());
        empleado.setCategoriaProfesional(paso4DTO.getCategoriaProfesional());
        empleado.setSalarioBaseMensual(paso4DTO.getSalarioBaseMensual());
        empleado.setComplementoMensual(paso4DTO.getComplementoMensual());
        empleado.setDevengoPagaExtra(paso4DTO.getDevengoPagaExtra());
        empleado.setFechaIncorporacion(paso4DTO.getFechaIncorporacion());

        return empleado;
    }
}