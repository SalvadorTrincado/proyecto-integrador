package com.equipo.service;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    // --- Métodos para guardar empleados ---
    @Transactional
    public Empleado guardarEmpleado(Empleado empleado) {
        // Aquí podrías añadir lógica de negocio o validaciones antes de guardar
        return empleadoRepository.save(empleado);
    }

    // --- Métodos para obtener empleados ---
    @Transactional(readOnly = true)
    public Optional<Empleado> obtenerEmpleadoPorId(UUID id) {
        return empleadoRepository.findById(id);
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