package com.equipo.service;

import com.equipo.entity.Empleado;
import com.equipo.repository.EmpleadoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Crear un empleado de prueba si no hay ninguno
    @PostConstruct
    public void crearEmpleadoDePrueba() {
        if (empleadoRepository.count() == 0) {
            Empleado e = new Empleado();
            e.setNombre("Ana");
            e.setApellidos("López");
            e.setGeneroSeleccionado("F");
            e.setFechaNacimiento(LocalDate.of(1993, 1, 1));
            e.setEdad(30);
            e.setPaisNacimiento("España");
            e.setDocumento("12345678A");
            e.setTipoDocumento("DNI");
            e.setPrefijoTelefono("+34");
            e.setTelefonoMovil("600111222");
            e.setDepartamento("Recursos Humanos");
            e.setNumeroCuenta("ES7620770024003102575766");
            e.setTipoContrato("Indefinido");
            e.setCategoriaProfesional("Administrativo");
            e.setSalarioBaseMensual(1500.00);
            e.setComplementoMensual(200.00);
            e.setDevengoPagaExtra("Junio/Diciembre");
            e.setFechaIncorporacion(LocalDate.of(2023, 3, 1));

            empleadoRepository.save(e);

            System.out.println("✅ Empleado de prueba creado: " + e.getNombre() + " " + e.getApellidos());
            System.out.println("🆔 UUID: " + e.getId());
        }
    }

    /*
    // Mostrar todos los empleados y sus etiquetas
    public void mostrarEmpleadosConEtiquetas() {
        List<Empleado> empleados = empleadoRepository.findAll();
        System.out.println("📋 Empleados actuales en la base de datos:");
        for (Empleado e : empleados) {
            System.out.println("- " + e.getNombre() + " " + e.getApellidos() + " (UUID: " + e.getId() + ")");
            if (e.getEtiquetas() != null && !e.getEtiquetas().isEmpty()) {
                e.getEtiquetas().forEach(et -> System.out.println("    • " + et.getNombre()));
            } else {
                System.out.println("    • Sin etiquetas");
            }
        }
    }*/
}
