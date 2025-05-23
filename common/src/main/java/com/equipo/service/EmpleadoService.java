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
            // Sin @GeneratedValue, save() con un ID asignado y que no existe en BD
            // debería realizar un INSERT directamente (o un persist bajo el capó).
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
        logger.info("Intentando guardar/actualizar empleado con ID: {}", empleado.getId());
        // Si el ID es nulo y no hay @GeneratedValue, esto fallaría a menos que la BD tenga un default para el PK.
        // Pero en nuestro caso, para Empleado, esperamos que el ID siempre venga asignado.
        // Si app-admin usa esto para actualizar, estará bien. Si lo usa para crear, necesitará asignar ID.
        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        logger.info("Empleado guardado/actualizado con ID: {}", empleadoGuardado.getId());
        return empleadoGuardado;
    }

    @Transactional(readOnly = true)
    public Optional<Empleado> obtenerEmpleadoPorId(UUID id) {
        return empleadoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Empleado> buscarEmpleadosPorParametros(String nombre, String departamento, Double salarioMinimo, Double salarioMaximo) {
        return empleadoRepository.buscarPorParametros(nombre, departamento, salarioMinimo, salarioMaximo);
    }

    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll();
    }

    public List<Empleado> buscarYOrdenar(String filtro, String ordenarPor) {
        List<Empleado> empleados = empleadoRepository.buscarEmpleadosPorNombreConteniendo(filtro);

        return empleados.stream()
                .sorted((e1, e2) -> {
                    switch (ordenarPor) {
                        case "fechaIngreso":
                            return e1.getFechaIncorporacion().compareTo(e2.getFechaIncorporacion());
                        case "nombre":
                        default:
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
        // La entidad ya no tiene el campo directo 'especialidades' si se quitó la prueba de aislamiento.
        // Si se restauró 'especialidadesSeleccionadas', este método es correcto.
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