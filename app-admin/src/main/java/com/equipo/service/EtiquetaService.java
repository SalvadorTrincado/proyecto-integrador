package com.equipo.service;

import com.equipo.entity.Empleado;
import com.equipo.entity.Etiqueta;
import com.equipo.repository.EmpleadoRepository;
import com.equipo.repository.EtiquetaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;
    private final EmpleadoRepository empleadoRepository;

    @Autowired
    public EtiquetaService(EtiquetaRepository etiquetaRepository, EmpleadoRepository empleadoRepository) {
        this.etiquetaRepository = etiquetaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public Etiqueta crearOActualizarEtiqueta(Etiqueta etiqueta) {
        if (etiqueta.getNombre() == null || etiqueta.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la etiqueta no puede ser vacío.");
        }
        // Normalizar el nombre para evitar duplicados por capitalización o espacios
        String nombreNormalizado = etiqueta.getNombre().trim();
        etiqueta.setNombre(nombreNormalizado);

        if (etiqueta.getId() == null) { // Creando nueva etiqueta
            Optional<Etiqueta> existente = etiquetaRepository.findByNombre(nombreNormalizado);
            if (existente.isPresent()) {
                throw new IllegalArgumentException("Ya existe una etiqueta con el nombre: " + nombreNormalizado);
            }
        } else { // Actualizando etiqueta existente
            Optional<Etiqueta> existenteConMismoNombre = etiquetaRepository.findByNombre(nombreNormalizado);
            if (existenteConMismoNombre.isPresent() && !existenteConMismoNombre.get().getId().equals(etiqueta.getId())) {
                throw new IllegalArgumentException("Ya existe otra etiqueta con el nombre: " + nombreNormalizado);
            }
        }
        return etiquetaRepository.save(etiqueta);
    }

    @Transactional(readOnly = true)
    public List<Etiqueta> obtenerTodasLasEtiquetas() {
        return etiquetaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Etiqueta> obtenerEtiquetaPorId(UUID id) {
        return etiquetaRepository.findById(id);
    }

    @Transactional
    public void eliminarEtiqueta(UUID id) {
        Etiqueta etiqueta = etiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + id));

        // Antes de eliminar la etiqueta, hay que desvincularla de todos los empleados
        for (Empleado empleado : etiqueta.getEmpleados()) {
            empleado.getEtiquetas().remove(etiqueta);
            // No es necesario llamar a empleadoRepository.save(empleado) aquí si
            // la gestión de la relación es propiedad de Empleado y se usa orphanRemoval
            // o si se guarda explícitamente después. Sin embargo, como la relación
            // es ManyToMany y la tabla de unión es la que gestiona,
            // simplemente eliminar la etiqueta debería ser suficiente para que JPA
            // elimine las entradas de la tabla de unión.
        }
        etiqueta.getEmpleados().clear(); // Limpia la colección en el lado de la etiqueta
        etiquetaRepository.delete(etiqueta);
    }

    @Transactional
    public Empleado asignarEtiquetasAEmpleado(UUID empleadoId, Set<UUID> idsEtiquetas) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));

        Set<Etiqueta> nuevasEtiquetas = idsEtiquetas.stream()
                .map(idEtiqueta -> etiquetaRepository.findById(idEtiqueta)
                        .orElseThrow(() -> new EntityNotFoundException("Etiqueta no encontrada con ID: " + idEtiqueta)))
                .collect(Collectors.toSet());

        // Para manejar la desasignación de etiquetas que ya no están en el nuevo conjunto:
        // Primero, limpiamos las etiquetas existentes del empleado y actualizamos el lado inverso
        empleado.getEtiquetas().forEach(etiquetaExistente -> etiquetaExistente.getEmpleados().remove(empleado));
        empleado.getEtiquetas().clear();

        // Luego, añadimos las nuevas etiquetas y actualizamos el lado inverso
        nuevasEtiquetas.forEach(nuevaEtiqueta -> {
            empleado.addEtiqueta(nuevaEtiqueta); // addEtiqueta se encarga de la bidireccionalidad
        });

        return empleadoRepository.save(empleado);
    }

    @Transactional
    public Empleado desasignarEtiquetasDeEmpleado(UUID empleadoId, Set<UUID> idsEtiquetasADesasignar) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));

        List<Etiqueta> etiquetasADesasignar = etiquetaRepository.findAllById(idsEtiquetasADesasignar);

        for (Etiqueta etiqueta : etiquetasADesasignar) {
            if (empleado.getEtiquetas().contains(etiqueta)) {
                empleado.removeEtiqueta(etiqueta); // removeEtiqueta maneja la bidireccionalidad
            }
        }
        return empleadoRepository.save(empleado);
    }


    @Transactional(readOnly = true)
    public List<Etiqueta> buscarEtiquetasPorNombre(String nombre) {
        return etiquetaRepository.findByNombreStartingWithIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public Set<Etiqueta> obtenerEtiquetasDeEmpleado(UUID empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado con ID: " + empleadoId));
        // Devuelve una copia para evitar modificaciones directas de la colección persistida fuera de una transacción
        return new HashSet<>(empleado.getEtiquetas());
    }
}