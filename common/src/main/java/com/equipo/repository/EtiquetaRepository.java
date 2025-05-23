package com.equipo.repository;

import com.equipo.entity.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, UUID> {

    /**
     * Busca una etiqueta por su nombre.
     * @param nombre El nombre de la etiqueta a buscar.
     * @return Un Optional que contiene la etiqueta si se encuentra, o vacío si no.
     */
    Optional<Etiqueta> findByNombre(String nombre);

    /**
     * Busca etiquetas cuyo nombre comience con el texto proporcionado, ignorando mayúsculas y minúsculas.
     * Útil para funcionalidades de autocompletado.
     * @param nombre El prefijo del nombre de la etiqueta a buscar.
     * @return Una lista de etiquetas que coinciden con el criterio.
     */
    List<Etiqueta> findByNombreStartingWithIgnoreCase(String nombre);

    /**
     * Verifica si existe una etiqueta con el nombre proporcionado.
     * @param nombre El nombre de la etiqueta a verificar.
     * @return true si existe una etiqueta con ese nombre, false en caso contrario.
     */
    boolean existsByNombre(String nombre);
}