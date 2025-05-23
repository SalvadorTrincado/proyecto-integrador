package com.equipo.service;

import com.equipo.dto.NominaDetalleDTO; // Necesitaremos un DTO similar al de app-admin o reutilizarlo si es posible
import com.equipo.entity.Empleado;
import com.equipo.entity.LineaNomina;
import com.equipo.entity.Nomina;
import com.equipo.repository.EmpleadoRepository;
import com.equipo.repository.NominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException; // Para manejo de errores

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.equipo.dto.LineaNominaDTO; // Reutilizar el DTO de línea de nómina si es posible

@Service
public class NominaEmpleadoService {

    private final NominaRepository nominaRepository;
    private final EmpleadoRepository empleadoRepository; // Para obtener el empleado

    @Autowired
    public NominaEmpleadoService(NominaRepository nominaRepository, EmpleadoRepository empleadoRepository) {
        this.nominaRepository = nominaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional(readOnly = true)
    public Page<NominaDetalleDTO> findNominasByEmpleado(UUID empleadoId, Pageable pageable) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));

        // Obtener solo las nóminas que ya han sido "cerradas" o "cobradas" (ej. fechaFinPeriodo es anterior al mes actual)
        // O todas si no hay tal restricción para la vista del empleado.
        // Por ahora, mostramos todas las del empleado.
        Page<Nomina> nominasPagina = nominaRepository.findByEmpleado(empleado, pageable);
        return nominasPagina.map(this::convertToDetalleDto);
    }

    @Transactional(readOnly = true)
    public Optional<NominaDetalleDTO> findNominaDetalleByIdAndEmpleadoId(UUID nominaId, UUID empleadoId) {
        Nomina nomina = nominaRepository.findById(nominaId)
                .orElseThrow(() -> new EntityNotFoundException("Nómina no encontrada con ID: " + nominaId));

        // Validar que la nómina pertenece al empleado
        if (!nomina.getEmpleado().getId().equals(empleadoId)) {
            // Podrías lanzar una excepción de seguridad o simplemente devolver Optional.empty()
            throw new SecurityException("Acceso denegado: Esta nómina no pertenece al empleado especificado.");
        }
        return Optional.of(convertToDetalleDto(nomina));
    }

    // Método para convertir Nomina a NominaDetalleDTO (similar al de NominaService en app-admin)
    // Si NominaDetalleDTO y LineaNominaDTO están en common, se pueden usar directamente.
    // Si no, hay que crearlos en app-empleados o en common.
    // Asumiré que podemos usar/crear DTOs similares a los de app-admin.
    // Si los DTOs de app-admin (NominaDetalleDTO, LineaNominaDTO) están en un módulo `common-dto` o
    // son accesibles, se pueden reutilizar. Si están dentro de `app-admin/src/main/java/com/equipo/dto`,
    // tendrías que duplicarlos o moverlos a `common`.
    // Por ahora, asumiré que puedes crear/usar DTOs equivalentes.
    // Este método de conversión es crucial y debe ser igual o similar al de app-admin.
    private NominaDetalleDTO convertToDetalleDto(Nomina nomina) {
        if (nomina == null) return null;
        NominaDetalleDTO dto = new NominaDetalleDTO(); // Asegúrate que esta clase DTO exista y sea accesible
        dto.setId(nomina.getId().toString());
        if (nomina.getEmpleado() != null) {
            dto.setEmpleadoId(nomina.getEmpleado().getId().toString());
            // Usar los campos snapshot de la nómina
            dto.setNombreCompletoEmpleado(nomina.getNombreCompletoEmpleado());
            dto.setIdentificacionEmpleado(nomina.getIdentificacionEmpleado());
            dto.setPuestoProfesionalEmpleado(nomina.getPuestoProfesionalEmpleado());
            dto.setDepartamentoEmpleado(nomina.getDepartamentoEmpleado());
            dto.setFechaAltaEmpleado(nomina.getFechaAltaEmpleado());
        }
        dto.setFechaInicioPeriodo(nomina.getFechaInicioPeriodo());
        dto.setFechaFinPeriodo(nomina.getFechaFinPeriodo());
        dto.setNombreEmpresa(nomina.getNombreEmpresa());
        dto.setCifEmpresa(nomina.getCifEmpresa());
        dto.setDireccionEmpresa(nomina.getDireccionEmpresa());

        dto.setLineasNomina(nomina.getLineasNomina().stream()
                .map(this::convertLineaToDto)
                .collect(Collectors.toList()));

        dto.setTotalDevengos(nomina.getTotalDevengos());
        dto.setTotalDeducciones(nomina.getTotalDeducciones());
        dto.setNetoAPercibir(nomina.getNetoAPercibir());

        // Campos opcionales para acumulados anuales
        dto.setBrutoAcumuladoAnual(nomina.getBrutoAcumuladoAnual());
        dto.setRetencionesAcumuladasAnual(nomina.getRetencionesAcumuladasAnual());
        dto.setPercibidoAcumuladoAnual(nomina.getPercibidoAcumuladoAnual());

        return dto;
    }

    private LineaNominaDTO convertLineaToDto(LineaNomina linea) { // Asegúrate que esta clase DTO exista y sea accesible
        if (linea == null) return null;
        LineaNominaDTO dto = new LineaNominaDTO();
        if (linea.getId() != null) {
            dto.setId(linea.getId().toString());
        }
        dto.setConcepto(linea.getConcepto());
        dto.setPorcentaje(linea.getPorcentaje());
        dto.setCantidad(linea.getCantidad());
        dto.setTipo(linea.getTipo()); // Asume que LineaNomina.TipoLinea es accesible
        return dto;
    }
}