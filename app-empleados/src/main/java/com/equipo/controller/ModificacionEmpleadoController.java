package com.equipo.controller;

import com.equipo.dto.ModificacionEmpleadoDTO;
import com.equipo.entity.Empleado;
import com.equipo.entity.Usuario;
import com.equipo.service.EmpleadoService;
import com.equipo.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/empleado") // Prefijo para las rutas de empleado
public class ModificacionEmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(ModificacionEmpleadoController.class);
    // Define el directorio de subida. Ajusta la ruta según tu estructura y configuración.
    // Considera hacerlo configurable a través de application.properties.
    private static final String UPLOAD_DIR = "uploads/fotografias/";

    private final EmpleadoService empleadoService;
    private final UsuarioService usuarioService;

    @Autowired
    public ModificacionEmpleadoController(EmpleadoService empleadoService, UsuarioService usuarioService) {
        this.empleadoService = empleadoService;
        this.usuarioService = usuarioService;
    }

    private UUID obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal().toString())) {
            Object principal = authentication.getPrincipal();
            String username;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            Optional<Usuario> usuarioOpt = usuarioService.obtenerUsuarioPorEmail(username);
            return usuarioOpt.map(Usuario::getId).orElse(null);
        }
        return null;
    }

    @GetMapping("/modificar-datos")
    public String mostrarFormularioModificacion(Model model, RedirectAttributes redirectAttributes) {
        UUID empleadoId = obtenerUsuarioIdAutenticado();
        if (empleadoId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Usuario no autenticado. Por favor, inicie sesión.");
            return "redirect:/autenticacion/paso1";
        }

        Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(empleadoId);
        if (empleadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Perfil de empleado no encontrado. Si es un nuevo registro, por favor complete el proceso de alta de empleado.");
            // Redirigir al área personal, que a su vez podría redirigir al paso 1 de registro si es necesario
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        model.addAttribute("modificacionEmpleadoDTO", new ModificacionEmpleadoDTO(empleadoOpt.get()));
        return "aplicacion_corporativa/empleado/modificar_datos_empleado";
    }

    @PostMapping("/modificar-datos")
    public String procesarModificacionDatos(@Valid @ModelAttribute("modificacionEmpleadoDTO") ModificacionEmpleadoDTO dto,
                                            BindingResult result,
                                            Model model, // Usar Model para errores en la misma página
                                            RedirectAttributes redirectAttributes) {
        UUID empleadoId = obtenerUsuarioIdAutenticado();
        if (empleadoId == null) {
            redirectAttributes.addFlashAttribute("errorGlobal", "Sesión inválida o expirada. Por favor, inicie sesión de nuevo.");
            return "redirect:/autenticacion/paso1";
        }

        if (result.hasErrors()) {
            model.addAttribute("modificacionEmpleadoDTO", dto); // Devolver el DTO con errores para repoblar el formulario
            // No añadir errorGlobal aquí si los errores son específicos de campo, Thymeleaf los mostrará
            return "aplicacion_corporativa/empleado/modificar_datos_empleado";
        }

        Optional<Empleado> empleadoOpt = empleadoService.obtenerEmpleadoPorId(empleadoId);
        if (empleadoOpt.isEmpty()) {
            // Esto no debería ocurrir si el GET funcionó, pero es una salvaguarda
            redirectAttributes.addFlashAttribute("errorGlobal", "Error crítico: Perfil de empleado no encontrado durante la actualización.");
            return "redirect:/aplicacion_corporativa/area_personal";
        }

        Empleado empleado = empleadoOpt.get();

        // Mapear campos del DTO a la entidad Empleado
        empleado.setNombre(dto.getNombre());
        empleado.setApellidos(dto.getApellidos());
        empleado.setGeneroSeleccionado(dto.getGeneroSeleccionado());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setEdad(dto.getEdad());
        empleado.setPaisNacimiento(dto.getPaisNacimiento());
        empleado.setComentarios(dto.getComentarios());

        // Campos de contacto
        empleado.setPrefijoTelefono(dto.getPrefijoTelefono());
        empleado.setTelefonoMovil(dto.getTelefonoMovil());
        empleado.setTipoViaDireccionPpal(dto.getTipoViaDireccionPpal());
        empleado.setNombreViaDireccionPpal(dto.getNombreViaDireccionPpal());
        empleado.setNumeroViaDireccionPpal(dto.getNumeroViaDireccionPpal());
        empleado.setPortalDireccionPpal(dto.getPortalDireccionPpal());
        empleado.setPlantaDireccionPpal(dto.getPlantaDireccionPpal());
        empleado.setPuertaDireccionPpal(dto.getPuertaDireccionPpal());
        empleado.setLocalidadDireccionPpal(dto.getLocalidadDireccionPpal());
        empleado.setRegionDireccionPpal(dto.getRegionDireccionPpal());
        empleado.setCodigoPostalDireccionPpal(dto.getCodigoPostalDireccionPpal());

        // Manejo de la fotografía (si se proporcionó una nueva)
        MultipartFile fotografiaFile = dto.getFotografia();
        if (fotografiaFile != null && !fotografiaFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    logger.info("Directorio de subida creado en: {}", uploadPath.toAbsolutePath());
                }

                // Considerar borrar la foto antigua si existe y se va a reemplazar.
                // String fotoAntigua = empleado.getFotografia();

                String originalFileName = fotografiaFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + (originalFileName != null ? originalFileName.replaceAll("\\s+", "_") : "foto.jpg");
                Path filePath = uploadPath.resolve(uniqueFileName);

                fotografiaFile.transferTo(filePath.toFile()); // Usar transferTo que es más directo

                empleado.setFotografia(uniqueFileName); // Guardar el nombre del nuevo archivo
                logger.info("Fotografía actualizada para el empleado {}: {}", empleadoId, uniqueFileName);

                // if (fotoAntigua != null && !fotoAntigua.isEmpty()) {
                //     try {
                //         Files.deleteIfExists(uploadPath.resolve(fotoAntigua));
                //         logger.info("Foto antigua {} eliminada.", fotoAntigua);
                //     } catch (IOException e) {
                //         logger.error("No se pudo eliminar la foto antigua {}: {}", fotoAntigua, e.getMessage());
                //     }
                // }

            } catch (IOException e) {
                logger.error("Error al guardar la fotografía para el empleado {}: {}", empleadoId, e.getMessage(), e);
                model.addAttribute("modificacionEmpleadoDTO", dto);
                model.addAttribute("errorGlobal", "Error al subir la fotografía: " + e.getMessage() + ". Verifique los permisos del directorio: " + Paths.get(UPLOAD_DIR).toAbsolutePath());
                return "aplicacion_corporativa/empleado/modificar_datos_empleado";
            }
        }

        try {
            empleadoService.guardarOActualizarEmpleado(empleado);
            redirectAttributes.addFlashAttribute("mensajeExitoGlobal", "Sus datos han sido actualizados correctamente.");
            return "redirect:/aplicacion_corporativa/area_personal";
        } catch (Exception e) {
            logger.error("Error al guardar los cambios para el empleado {}: {}", empleadoId, e.getMessage(), e);
            model.addAttribute("modificacionEmpleadoDTO", dto);
            model.addAttribute("errorGlobal", "Ocurrió un error al guardar los cambios: " + e.getMessage());
            return "aplicacion_corporativa/empleado/modificar_datos_empleado";
        }
    }
}