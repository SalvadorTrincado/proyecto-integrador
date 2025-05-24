package com.equipo.service;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Empleado registrarNuevoEmpleadoConIdAsignado(Empleado empleado) {
        if (empleado.getId() == null) {
            logger.error("Error al registrar empleado: ID nulo proporcionado.");
            throw new IllegalArgumentException("El ID del empleado no puede ser nulo para este método de registro.");
        }
        logger.info("Intentando registrar NUEVO empleado con ID pre-asignado: {}", empleado.getId());

        if (empleadoRepository.existsById(empleado.getId())) {
            logger.warn("Error: Intento de registrar empleado con ID {} que ya existe.", empleado.getId());
            throw new IllegalStateException("Ya existe un empleado registrado con el ID: " + empleado.getId() + ". El proceso de registro no puede continuar.");
        }

        try {
            logger.info("Llamando a empleadoRepository.save() para empleado ID: {}", empleado.getId());
            Empleado empleadoGuardado = empleadoRepository.save(empleado);
            logger.info("Empleado guardado (después de save(), antes de commit de transacción) con ID: {}", empleadoGuardado.getId());
            return empleadoGuardado;
        } catch (Exception e) {
            logger.error("Excepción DURANTE empleadoRepository.save() para empleado ID {}: {}", empleado.getId(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Empleado guardarOActualizarEmpleado(Empleado empleado) {
        logger.info("Servicio: Guardando o actualizando empleado con ID: {}", empleado.getId());
        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        logger.info("Servicio: Empleado guardado/actualizado con ID: {}", empleadoGuardado.getId());
        return empleadoGuardado;
    }

    @Transactional(readOnly = true)
    public Optional<Empleado> obtenerEmpleadoPorId(UUID id) {
        logger.debug("Servicio: Obteniendo empleado por ID: {}", id);
        return empleadoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorParametros(String nombre, String departamento, Double salarioMinimo, Double salarioMaximo) {
        logger.info("Servicio: Llamando a Repositorio.buscarPorParametros: nombre='{}', departamento='{}', salarioMinimo={}, salarioMaximo={}",
                nombre, departamento, salarioMinimo, salarioMaximo);
        List<Empleado> resultados = empleadoRepository.buscarPorParametros(nombre, departamento, salarioMinimo, salarioMaximo);
        logger.info("Servicio: Repositorio.buscarPorParametros devolvió {} empleados.", (resultados != null ? resultados.size() : "null"));
        // if (resultados != null) {
        //    logger.debug("IDs de empleados por parámetro: {}", resultados.stream().map(Empleado::getId).collect(Collectors.toList()));
        // }
        return resultados;
    }

    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodosLosEmpleados() {
        logger.info("Servicio: Llamando a Repositorio.findAll().");
        List<Empleado> resultados = empleadoRepository.findAll();
        logger.info("Servicio: Repositorio.findAll() devolvió {} empleados.", (resultados != null ? resultados.size() : "null"));
        // if (resultados != null) {
        //     logger.debug("IDs de todos los empleados: {}", resultados.stream().map(Empleado::getId).collect(Collectors.toList()));
        // }
        return resultados;
    }

    public List<Empleado> buscarYOrdenar(String filtro, String ordenarPor) {
        logger.debug("Servicio: Buscando y ordenando empleados con filtro '{}' y orden '{}'", filtro, ordenarPor);
        List<Empleado> empleados = empleadoRepository.buscarEmpleadosPorNombreConteniendo(filtro);

        return empleados.stream()
                .sorted((e1, e2) -> {
                    switch (ordenarPor) {
                        case "fechaIngreso":
                            if (e1.getFechaIncorporacion() == null && e2.getFechaIncorporacion() == null) return 0;
                            if (e1.getFechaIncorporacion() == null) return 1; // nulls al final
                            if (e2.getFechaIncorporacion() == null) return -1; // nulls al final
                            return e1.getFechaIncorporacion().compareTo(e2.getFechaIncorporacion());
                        case "nombre":
                        default:
                            if (e1.getNombre() == null && e2.getNombre() == null) return 0;
                            if (e1.getNombre() == null) return 1;
                            if (e2.getNombre() == null) return -1;
                            return e1.getNombre().compareToIgnoreCase(e2.getNombre());
                    }
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorNombre(String nombre) {
        return empleadoRepository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorApellidos(String apellidos) {
        return empleadoRepository.findByApellidos(apellidos);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorDepartamento(String departamento) {
        return empleadoRepository.findByDepartamento(departamento);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorEspecialidad(String especialidad) {
        return empleadoRepository.findByEspecialidadesSeleccionadasContaining(especialidad);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorNombreConteniendo(String nombre) {
        return empleadoRepository.buscarEmpleadosPorNombreConteniendo(nombre);
    }

    @Transactional
    public void eliminarEmpleado(UUID id) {
        empleadoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeEmpleado(UUID id) {
        return empleadoRepository.existsById(id);
    }
}