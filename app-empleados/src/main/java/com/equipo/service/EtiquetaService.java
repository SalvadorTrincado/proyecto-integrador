/*package com.equipo.service;

import com.equipo.entity.Empleado;
import com.equipo.entity.Etiqueta;
import com.equipo.repository.EmpleadoRepository;
import com.equipo.repository.EtiquetaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EtiquetaService {

    @Autowired
    private EtiquetaRepository etiquetaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public Etiqueta crearSiNoExiste(String nombre) {
        return etiquetaRepository.findByNombre(nombre)
                .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombre)));
    }

    @Transactional
    public void asignarEtiquetaAEmpleado(UUID idEmpleado, String nombreEtiqueta) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(idEmpleado);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();
            Etiqueta etiqueta = etiquetaRepository.findByNombre(nombreEtiqueta)
                    .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombreEtiqueta)));

            // Aseguramos que la relación se actualiza por ambos lados
            empleado.getEtiquetas().add(etiqueta);
            etiqueta.getEmpleados().add(empleado);  // Añadir este lado si existe

            empleadoRepository.save(empleado);
            etiquetaRepository.save(etiqueta); // Guardamos también la etiqueta por si es nueva
        }
    }

    public List<String> buscarPorTexto(String texto) {
        return etiquetaRepository.findByNombreStartingWithIgnoreCase(texto)
                .stream()
                .map(Etiqueta::getNombre)
                .sorted()
                .toList();
    }

    public void eliminarEtiquetasDeEmpleado(UUID idEmpleado, List<String> etiquetasSeleccionadas) {
        Optional<Empleado> empleadoOpt = empleadoRepository.findById(idEmpleado);
        if (empleadoOpt.isPresent()) {
            Empleado empleado = empleadoOpt.get();

            // Crear nuevo set con las etiquetas que se quieren conservar
            Set<Etiqueta> etiquetasAConservar = empleado.getEtiquetas()
                    .stream()
                    .filter(et -> etiquetasSeleccionadas.contains(et.getNombre()))
                    .collect(Collectors.toSet());

            // Reemplazar el set original (NO usar .clear() ni .remove())
            empleado.setEtiquetas(etiquetasAConservar);

            empleadoRepository.save(empleado);
        }
    }


    public void asignarEtiquetasAMultiplesEmpleados(List<UUID> idsEmpleados, List<String> nombresEtiquetas) {
        List<Empleado> empleados = empleadoRepository.findAllById(idsEmpleados);

        List<Etiqueta> etiquetas = nombresEtiquetas.stream()
                .map(nombre -> etiquetaRepository.findByNombre(nombre.trim())
                        .orElseGet(() -> etiquetaRepository.save(new Etiqueta(nombre.trim())))
                ).toList();

        for (Empleado empleado : empleados) {
            empleado.getEtiquetas().addAll(etiquetas);
        }

        empleadoRepository.saveAll(empleados);
    }


}

*/

