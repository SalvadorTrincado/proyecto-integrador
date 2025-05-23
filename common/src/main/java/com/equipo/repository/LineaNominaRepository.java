package com.equipo.repository;

import com.equipo.entity.LineaNomina;
import com.equipo.entity.Nomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LineaNominaRepository extends JpaRepository<LineaNomina, UUID> {

    List<LineaNomina> findByNomina(Nomina nomina);

    // Para verificar si un concepto ya existe en una nómina [cite: 159]
    Optional<LineaNomina> findByNominaAndConceptoIgnoreCase(Nomina nomina, String concepto);

    // Para eliminar todas las líneas de una nómina (si no se usa CascadeType.ALL y orphanRemoval)
    void deleteByNomina(Nomina nomina);
}