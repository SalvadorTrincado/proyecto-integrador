package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList; // Importar por si se inicializa la lista aquí
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "EMPLEADOS")
@Data
public class Empleado {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO) // <-- LÍNEA ELIMINADA/COMENTADA
    private UUID id;

    // PASO 1: Datos personales
    private String nombre;
    private String apellidos;
    private String fotografia;
    private String generoSeleccionado;
    private LocalDate fechaNacimiento;
    private Integer edad;
    private String paisNacimiento;
    @Column(length = 500)
    private String comentarios;

    // PASO 2: Datos de contacto
    private String tipoDocumento;
    private String documento;
    private String prefijoTelefono;
    private String telefonoMovil;
    private String tipoViaDireccionPpal;
    private String nombreViaDireccionPpal;
    private String numeroViaDireccionPpal;
    private String portalDireccionPpal;
    private String plantaDireccionPpal;
    private String puertaDireccionPpal;
    private String localidadDireccionPpal;
    private String regionDireccionPpal;
    private String codigoPostalDireccionPpal;

    // PASO 3: Datos profesionales
    private String departamento;
    @ElementCollection(fetch = FetchType.LAZY) // fetch = FetchType.LAZY es el default, pero puede ser explícito
    @CollectionTable(name = "empleado_especialidades", joinColumns = @JoinColumn(name = "empleado_id"))
    @Column(name = "especialidad")
    private List<String> especialidadesSeleccionadas = new ArrayList<>(); // Inicializar aquí es buena práctica

    // PASO 4: Datos económicos
    private String numeroCuenta;
    private String tipoContrato;
    private String categoriaProfesional;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String devengoPagaExtra;
    private LocalDate fechaIncorporacion;

}