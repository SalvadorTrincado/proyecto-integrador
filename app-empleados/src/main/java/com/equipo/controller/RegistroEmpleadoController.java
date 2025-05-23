package com.equipo.controller;

import com.equipo.dto.RegistroEmpleadoPaso1DTO;
import com.equipo.dto.RegistroEmpleadoPaso2DTO;
import com.equipo.dto.RegistroEmpleadoPaso3DTO;
import com.equipo.dto.RegistroEmpleadoPaso4DTO;
import com.equipo.entity.Empleado;
import com.equipo.service.EmpleadoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class RegistroEmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroEmpleadoController.class);
    private final EmpleadoService empleadoService;

    @Autowired
    public RegistroEmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @GetMapping("/registro_empleado_paso1")
    public String mostrarFormularioPaso1(Model model, HttpSession session) {
        UUID usuarioIdAutenticado = (UUID) session.getAttribute("usuarioAutenticadoId");
        if (usuarioIdAutenticado != null && empleadoService.obtenerEmpleadoPorId(usuarioIdAutenticado).isPresent()) {
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        RegistroEmpleadoPaso1DTO paso1DTO = (RegistroEmpleadoPaso1DTO) session.getAttribute("registroEmpleadoPaso1DTO");
        model.addAttribute("registroEmpleadoPaso1DTO", (paso1DTO != null) ? paso1DTO : new RegistroEmpleadoPaso1DTO());
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/registro_empleado_paso1";
    }

    @PostMapping("/registro_empleado_paso1-post")
    public String procesarFormularioPaso1(@Valid @ModelAttribute("registroEmpleadoPaso1DTO") RegistroEmpleadoPaso1DTO paso1,
                                          BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            model.addAttribute("registroEmpleadoPaso1DTO", paso1);
            return "aplicacion_corporativa/registro/registro_empleado_paso1";
        }
        session.setAttribute("registroEmpleadoPaso1DTO", paso1);
        return "redirect:/registro_empleado_paso2";
    }

    @GetMapping("/registro_empleado_paso2")
    public String mostrarFormularioPaso2(Model model, HttpSession session) {
        RegistroEmpleadoPaso2DTO paso2DTO = (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO");
        model.addAttribute("registroEmpleadoPaso2DTO", (paso2DTO != null) ? paso2DTO : new RegistroEmpleadoPaso2DTO());
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/registro_empleado_paso2";
    }

    @PostMapping("/registro_empleado_paso2-post")
    public String procesarFormularioPaso2(@Valid @ModelAttribute("registroEmpleadoPaso2DTO") RegistroEmpleadoPaso2DTO paso2,
                                          BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            model.addAttribute("registroEmpleadoPaso2DTO", paso2);
            return "aplicacion_corporativa/registro/registro_empleado_paso2";
        }
        session.setAttribute("registroEmpleadoPaso2DTO", paso2);
        return "redirect:/registro_empleado_paso3";
    }

    @GetMapping("/registro_empleado_paso3")
    public String mostrarFormularioPaso3(Model model, HttpSession session) {
        RegistroEmpleadoPaso3DTO paso3DTO = (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO");
        model.addAttribute("registroEmpleadoPaso3DTO", (paso3DTO != null) ? paso3DTO : new RegistroEmpleadoPaso3DTO());

        List<String> especialidadesPosibles = List.of("Análisis de datos", "Administración de servidores", "Seguridad informática", "Desarrollo backend", "Desarrollo frontend", "Diseño UI/UX");
        model.addAttribute("especialidadesPosibles", especialidadesPosibles);
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/registro_empleado_paso3";
    }

    @PostMapping("/registro_empleado_paso3-post")
    public String procesarFormularioPaso3(@Valid @ModelAttribute("registroEmpleadoPaso3DTO") RegistroEmpleadoPaso3DTO paso3,
                                          BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            List<String> especialidadesPosibles = List.of("Análisis de datos", "Administración de servidores", "Seguridad informática", "Desarrollo backend", "Desarrollo frontend", "Diseño UI/UX");
            model.addAttribute("especialidadesPosibles", especialidadesPosibles);
            model.addAttribute("mostrarErrores", true);
            model.addAttribute("registroEmpleadoPaso3DTO", paso3);
            return "aplicacion_corporativa/registro/registro_empleado_paso3";
        }
        session.setAttribute("registroEmpleadoPaso3DTO", paso3);
        return "redirect:/registro_empleado_paso4";
    }

    @GetMapping("/registro_empleado_paso4")
    public String mostrarFormularioPaso4(Model model, HttpSession session) {
        RegistroEmpleadoPaso4DTO paso4DTO = (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO");
        model.addAttribute("registroEmpleadoPaso4DTO", (paso4DTO != null) ? paso4DTO : new RegistroEmpleadoPaso4DTO());
        model.addAttribute("mostrarErrores", false);
        return "aplicacion_corporativa/registro/registro_empleado_paso4";
    }

    @PostMapping("/registro_empleado_paso4-post")
    public String procesarFormularioPaso4(@Valid @ModelAttribute("registroEmpleadoPaso4DTO") RegistroEmpleadoPaso4DTO paso4DTO,
                                          BindingResult result, Model model, HttpSession session) {
        if (result.hasErrors()) {
            model.addAttribute("mostrarErrores", true);
            model.addAttribute("registroEmpleadoPaso4DTO", paso4DTO);
            return "aplicacion_corporativa/registro/registro_empleado_paso4";
        }
        session.setAttribute("registroEmpleadoPaso4DTO", paso4DTO);
        return "redirect:/resumen/exito";
    }

    @GetMapping("/resumen/exito")
    public String mostrarPaginaExito(HttpSession session, Model modelo, RedirectAttributes redirectAttributes) {
        RegistroEmpleadoPaso1DTO paso1DTO = (RegistroEmpleadoPaso1DTO) session.getAttribute("registroEmpleadoPaso1DTO");
        RegistroEmpleadoPaso2DTO paso2DTO = (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO");
        RegistroEmpleadoPaso3DTO paso3DTO = (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO");
        RegistroEmpleadoPaso4DTO paso4DTO = (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO");
        UUID usuarioIdAutenticado = (UUID) session.getAttribute("usuarioAutenticadoId");

        if (paso1DTO == null || paso2DTO == null || paso3DTO == null || paso4DTO == null || usuarioIdAutenticado == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Faltan datos de pasos anteriores o la sesión ha expirado. Por favor, comience el registro de nuevo.");
            return "redirect:/registro_empleado_paso1";
        }

        modelo.addAttribute("registroEmpleadoPaso1DTO", paso1DTO);
        modelo.addAttribute("registroEmpleadoPaso2DTO", paso2DTO);
        modelo.addAttribute("registroEmpleadoPaso3DTO", paso3DTO);
        modelo.addAttribute("registroEmpleadoPaso4DTO", paso4DTO);

        return "aplicacion_corporativa/registro/registro_empleado_paso5";
    }

    @PostMapping("/resumen/exito-post")
    public String procesarPaginaExitoPost(HttpSession session, RedirectAttributes redirectAttributes) {
        UUID usuarioIdAutenticado = (UUID) session.getAttribute("usuarioAutenticadoId");
        RegistroEmpleadoPaso1DTO paso1DTO = (RegistroEmpleadoPaso1DTO) session.getAttribute("registroEmpleadoPaso1DTO");
        RegistroEmpleadoPaso2DTO paso2DTO = (RegistroEmpleadoPaso2DTO) session.getAttribute("registroEmpleadoPaso2DTO");
        RegistroEmpleadoPaso3DTO paso3DTO = (RegistroEmpleadoPaso3DTO) session.getAttribute("registroEmpleadoPaso3DTO");
        RegistroEmpleadoPaso4DTO paso4DTO = (RegistroEmpleadoPaso4DTO) session.getAttribute("registroEmpleadoPaso4DTO");

        if (usuarioIdAutenticado == null || paso1DTO == null || paso2DTO == null || paso3DTO == null || paso4DTO == null) {
            logger.warn("Intento de procesar /resumen/exito-post sin todos los datos de sesión. Usuario ID: {}", usuarioIdAutenticado);
            redirectAttributes.addFlashAttribute("errorGlobal", "Error al procesar el registro debido a datos incompletos en sesión. Por favor, inténtelo de nuevo desde el paso 1.");
            return "redirect:/registro_empleado_paso1";
        }

        try {
            logger.info("Creando entidad Empleado para usuario ID: {}", usuarioIdAutenticado);
            Empleado empleado = crearEmpleadoDesdeSesion(usuarioIdAutenticado, paso1DTO, paso2DTO, paso3DTO, paso4DTO);

            logger.info("Llamando a empleadoService.registrarNuevoEmpleadoConIdAsignado para empleado ID: {}", empleado.getId());
            empleadoService.registrarNuevoEmpleadoConIdAsignado(empleado);
            logger.info("Empleado registrado/persistido exitosamente para ID: {}", empleado.getId());

            session.removeAttribute("registroEmpleadoPaso1DTO");
            session.removeAttribute("registroEmpleadoPaso2DTO");
            session.removeAttribute("registroEmpleadoPaso3DTO");
            session.removeAttribute("registroEmpleadoPaso4DTO");

            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "¡Registro de empleado completado con éxito!");
            return "redirect:/aplicacion_corporativa/area_personal";

        } catch (IllegalStateException e) {
            logger.warn("IllegalStateException al registrar empleado (ID: {}): {}", usuarioIdAutenticado, e.getMessage());
            redirectAttributes.addFlashAttribute("errorGlobal", e.getMessage());
            return "redirect:/aplicacion_corporativa/area_personal";
        } catch (Exception e) {
            logger.error("Excepción general al procesar el registro del empleado para usuario ID {}: {}", usuarioIdAutenticado, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorGlobal", "Ocurrió un error inesperado al guardar los datos del empleado: " + e.getMessage() +". Por favor, revise los logs o intente más tarde.");
            return "redirect:/resumen/exito";
        }
    }

    private Empleado crearEmpleadoDesdeSesion(UUID usuarioId,
                                              RegistroEmpleadoPaso1DTO paso1DTO,
                                              RegistroEmpleadoPaso2DTO paso2DTO,
                                              RegistroEmpleadoPaso3DTO paso3DTO,
                                              RegistroEmpleadoPaso4DTO paso4DTO) {
        Empleado empleado = new Empleado();
        empleado.setId(usuarioId);

        empleado.setNombre(paso1DTO.getNombre());
        empleado.setApellidos(paso1DTO.getApellidos());
        if (paso1DTO.getFotografia() != null && !paso1DTO.getFotografia().isEmpty()) {
            empleado.setFotografia(paso1DTO.getFotografia().getOriginalFilename());
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

        // Restaurar la asignación normal de especialidades
        if (paso3DTO.getEspecialidadesSeleccionadas() != null) {
            empleado.setEspecialidadesSeleccionadas(new ArrayList<>(paso3DTO.getEspecialidadesSeleccionadas()));
        } else {
            empleado.setEspecialidadesSeleccionadas(new ArrayList<>());
        }

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