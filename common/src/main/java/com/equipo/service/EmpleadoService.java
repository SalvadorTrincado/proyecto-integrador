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

    // --- Métodos para guardar empleados ---
    @Transactional
    public Empleado guardarEmpleado(Empleado empleado) {
        // Aquí podrías añadir lógica de negocio o validaciones antes de guardar
        logger.info("Intentando guardar empleado: {}", empleado);
        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        logger.info("Empleado guardado con ID: {}", empleadoGuardado.getId());
        return empleadoGuardado;
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



    // --- Métodos para obtener empleados ---
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

    // Puedes añadir más métodos de búsqueda según las necesidades de tu aplicación

    // --- Métodos para eliminar empleados ---
    @Transactional
    public void eliminarEmpleado(UUID id) {
        empleadoRepository.deleteById(id);
    }

    // --- Otros métodos ---
    @Transactional(readOnly = true)
    public boolean existeEmpleado(UUID id) {
        return empleadoRepository.existsById(id);
    }

    // Aquí podrías añadir métodos para realizar operaciones más complejas
    // que involucren varios pasos o validaciones.
}