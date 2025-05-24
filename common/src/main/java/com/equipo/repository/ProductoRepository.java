package com.equipo.repository;

import com.equipo.entity.Producto;
import com.equipo.entity.Proveedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto> {

    // Método para buscar un producto por descripción y proveedor (para evitar duplicados en importación) [cite: 27]
    Optional<Producto> findByDescripcionAndProveedor(String descripcion, Proveedor proveedor);

    // Consulta para la funcionalidad 3.2 (Consulta parametrizada)
    // Esta es una consulta básica, se puede mejorar con Specifications para más flexibilidad
    @Query("SELECT p FROM Producto p LEFT JOIN p.categorias c WHERE " +
            "(:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) AND " +
            "(:proveedorId IS NULL OR p.proveedor.id = :proveedorId) AND " +
            "(:categoriaNombre IS NULL OR LOWER(c.nombre) = LOWER(:categoriaNombre)) AND " + // Para una sola categoría. Si son varias, necesitará IN y un join más complejo o subconsultas.
            "(:esPerecedero IS NULL OR p.esPerecedero = :esPerecedero)")
    Page<Producto> findByFiltrosCompletos( // Cambiado para paginación
                                           @Param("descripcion") String descripcion,
                                           @Param("proveedorId") Long proveedorId, // Asumiendo ID de proveedor como Long si es el ID de la entidad Proveedor. Ajustar si es UUID.
                                           @Param("categoriaNombre") String categoriaNombre, // Simplificado para una categoría. Para múltiples, usar List<String> y ajustar la query.
                                           @Param("esPerecedero") Boolean esPerecedero,
                                           Pageable pageable
    );

    // Los métodos findByNombreContainingIgnoreCase y findByFiltros originales pueden necesitar ajustes o ser reemplazados
    // por JpaSpecificationExecutor para cumplir con la consulta parametrizada del PDF [cite: 40, 42, 44, 46]

    // Elimino el findByFiltros anterior ya que JpaSpecificationExecutor es más adecuado para los requisitos del PDF
    // List<Producto> findByNombreContainingIgnoreCase(String nombre); // Puede ser útil para búsquedas simples.
}