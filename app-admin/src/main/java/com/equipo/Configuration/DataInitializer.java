package com.equipo.Configuration;

import com.equipo.entity.Proveedor;
import com.equipo.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProveedorRepository proveedorRepository;

    @Autowired
    public DataInitializer(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    @Transactional // Es buena práctica para operaciones que modifican la BD
    public void run(String... args) throws Exception {
        log.info("Iniciando la carga de proveedores iniciales...");

        // Lista de proveedores a crear
        List<ProveedorData> proveedoresACrear = Arrays.asList(
                new ProveedorData("TecnoGlobal", "B12345678", "Calle Falsa 123, Madrid", "910000001"),
                new ProveedorData("MueblesDeluxe", "A98765432", "Avenida Siempre Viva 742, Barcelona", "930000002"),
                new ProveedorData("LibrosEtc", "C55555555", "Plaza Mayor 1, Sevilla", "950000003"),
                new ProveedorData("RopaModerna", "D11223344", "Gran Vía 25, Valencia", "960000004"),
                new ProveedorData("AlimentacionSana", "E67890123", "Camino Huerta 7, Murcia", "968000005")
        );

        for (ProveedorData pData : proveedoresACrear) {
            crearProveedorSiNoExiste(pData);
        }

        log.info("Carga de proveedores iniciales completada. Total de proveedores en BD: {}", proveedorRepository.count());
    }

    private void crearProveedorSiNoExiste(ProveedorData data) {
        if (proveedorRepository.findByNombre(data.nombre()).isEmpty()) {
            Proveedor nuevoProveedor = new Proveedor();
            nuevoProveedor.setNombre(data.nombre());
            nuevoProveedor.setCif(data.cif());
            nuevoProveedor.setDireccion(data.direccion());
            nuevoProveedor.setTelefono(data.telefono());
            proveedorRepository.save(nuevoProveedor);
            log.info("Proveedor '{}' creado.", data.nombre());
        } else {
            log.info("Proveedor '{}' ya existe, no se creará de nuevo.", data.nombre());
        }
    }

    // Record simple para encapsular los datos del proveedor
    private record ProveedorData(String nombre, String cif, String direccion, String telefono) {}
}