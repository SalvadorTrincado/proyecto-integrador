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

    Optional<Producto> findByDescripcionAndProveedor(String descripcion, Proveedor proveedor);

    @Query("SELECT p FROM Producto p LEFT JOIN p.categorias c WHERE " +
            "(:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) AND " +
            "(:proveedorId IS NULL OR p.proveedor.id = :proveedorId) AND " + // Asumiendo que proveedorId es Long aquí
            "(:categoriaNombre IS NULL OR LOWER(c.nombre) = LOWER(:categoriaNombre)) AND " +
            "(:esPerecedero IS NULL OR p.esPerecedero = :esPerecedero)")
    Page<Producto> findByFiltrosCompletos(
            @Param("descripcion") String descripcion,
            @Param("proveedorId") Long proveedorId, // Sigue siendo Long según el archivo original
            @Param("categoriaNombre") String categoriaNombre,
            @Param("esPerecedero") Boolean esPerecedero,
            Pageable pageable
    );

    // NUEVO MÉTODO PARA DEVOLVER LISTA SIN PAGINACIÓN
    @Query("SELECT p FROM Producto p LEFT JOIN p.categorias c WHERE " +
            "(:descripcion IS NULL OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))) AND " +
            "(:proveedorId IS NULL OR p.proveedor.id = :proveedorId) AND " + // Asumiendo que proveedorId es Long aquí
            "(:categoriaNombre IS NULL OR LOWER(c.nombre) = LOWER(:categoriaNombre)) AND " +
            "(:esPerecedero IS NULL OR p.esPerecedero = :esPerecedero) " +
            "ORDER BY p.id ASC") // Añadir un orden por defecto si se desea
    List<Producto> findListByFiltrosCompletos( // Devuelve List<Producto>
                                               @Param("descripcion") String descripcion,
                                               @Param("proveedorId") Long proveedorId, // Sigue siendo Long
                                               @Param("categoriaNombre") String categoriaNombre,
                                               @Param("esPerecedero") Boolean esPerecedero
    );
}