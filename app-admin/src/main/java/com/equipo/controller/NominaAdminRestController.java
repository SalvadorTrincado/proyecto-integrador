package com.equipo.controller;

import com.equipo.dto.AltaNominaDTO;
import com.equipo.dto.NominaDetalleDTO; // Asegúrate que el nombre de la clase sea NominaDetalleDTO
import com.equipo.entity.Nomina;
import com.equipo.service.NominaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/nominas")
public class NominaAdminRestController {

    private final NominaService nominaService;

    @Autowired
    public NominaAdminRestController(NominaService nominaService) {
        this.nominaService = nominaService;
    }

    @PostMapping
    public ResponseEntity<?> crearNomina(@Valid @RequestBody AltaNominaDTO altaNominaDto) {
        try {
            Nomina nuevaNomina = nominaService.crearNomina(altaNominaDto);
            NominaDetalleDTO detalleDto = nominaService.findNominaDetalleById(nuevaNomina.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo recuperar la nómina recién creada con ID: " + nuevaNomina.getId()));
            return ResponseEntity.status(HttpStatus.CREATED).body(detalleDto);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace(); // Es buena idea loggear el error completo en el servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado al crear la nómina."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalleNomina(@PathVariable String id) {
        try {
            UUID nominaId = UUID.fromString(id);
            Optional<NominaDetalleDTO> nominaDtoOptional = nominaService.findNominaDetalleById(nominaId);

            if (nominaDtoOptional.isPresent()) {
                return ResponseEntity.ok(nominaDtoOptional.get()); // Devuelve ResponseEntity<NominaDetalleDTO>
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Nómina no encontrada con ID: " + id)); // Devuelve ResponseEntity<Map<String, String>>
            }
        } catch (IllegalArgumentException e) { // Captura el error de UUID.fromString(id)
            return ResponseEntity.badRequest().body(Map.of("error", "El ID proporcionado (" + id + ") no es un UUID válido."));
        }
        // Considera añadir un catch más genérico si el servicio puede lanzar otras excepciones no esperadas
        // catch (Exception e) {
        // e.printStackTrace();
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener detalle de nómina."));
        // }
    }

    @GetMapping
    public ResponseEntity<Page<NominaDetalleDTO>> consultarNominas(
            @RequestParam(required = false) String empleadoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @PageableDefault(size = 10, sort = "fechaInicioPeriodo") Pageable pageable) {

        Page<NominaDetalleDTO> nominas = nominaService.findNominasByEmpleadoAndPeriodoForAdmin(empleadoId, fechaDesde, fechaHasta, pageable);
        return ResponseEntity.ok(nominas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarNomina(@PathVariable String id, @Valid @RequestBody AltaNominaDTO modificacionNominaDto) {
        try {
            UUID nominaId = UUID.fromString(id);
            Nomina nominaModificada = nominaService.modificarNomina(nominaId, modificacionNominaDto);
            NominaDetalleDTO detalleDto = nominaService.findNominaDetalleById(nominaModificada.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo recuperar la nómina recién modificada con ID: " + nominaModificada.getId()));
            return ResponseEntity.ok(detalleDto);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado al modificar la nómina."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNomina(@PathVariable String id) {
        try {
            UUID nominaId = UUID.fromString(id);
            nominaService.eliminarNomina(nominaId);
            return ResponseEntity.ok(Map.of("mensaje", "Nómina con ID " + id + " eliminada correctamente."));
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado al eliminar la nómina."));
        }
    }

    @DeleteMapping("/{nominaId}/lineas/{lineaNominaId}")
    public ResponseEntity<?> eliminarLineaDeNomina(@PathVariable String nominaId, @PathVariable String lineaNominaId) {
        try {
            UUID nominaUuid = UUID.fromString(nominaId);
            UUID lineaNominaUuid = UUID.fromString(lineaNominaId);
            Nomina nominaActualizada = nominaService.eliminarLineaDeNomina(nominaUuid, lineaNominaUuid);
            NominaDetalleDTO detalleDto = nominaService.findNominaDetalleById(nominaActualizada.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No se pudo recuperar la nómina tras eliminar la línea. ID: " + nominaActualizada.getId()));
            return ResponseEntity.ok(detalleDto);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado al eliminar la línea de la nómina."));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}