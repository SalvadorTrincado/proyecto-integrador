package com.equipo.backend.service;

import com.equipo.backend.dto.EmpleadoRegistroDTO;
import com.equipo.backend.entity.Empleado;
import com.equipo.backend.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public void registrarEmpleado(EmpleadoRegistroDTO empleadoDTO) {
        logger.info("Iniciando el registro del empleado: {}", empleadoDTO.getNombre());

        try {
            Empleado empleado = empleadoDTO.toEntity();
            empleadoRepository.save(empleado);

            logger.info("Empleado registrado exitosamente: {}", empleadoDTO.getNombre());
        } catch (Exception e) {
            logger.error("Error al registrar al empleado {}: {}", empleadoDTO.getNombre(), e.getMessage());
            throw e;
        }
    }
    public List<Empleado> obtenerSubordinados() {
        Empleado jefe = empleadoRepository.findById(1L).orElse(null);
        return jefe != null ? jefe.getSubordinados() : List.of();
    }

}