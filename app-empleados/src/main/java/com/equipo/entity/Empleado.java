package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "EMPLEADOS")
@Data
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // PASO 1: Datos personales
    private String nombre;
    private String apellidos;
    private String fotografia; // Guardaremos la ruta o el nombre del archivo
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
    @ElementCollection
    @CollectionTable(name = "empleado_especialidades", joinColumns = @JoinColumn(name = "empleado_id"))
    @Column(name = "especialidad")
    private List<String> especialidadesSeleccionadas;

    // PASO 4: Datos económicos
    private String numeroCuenta;
    private String tipoContrato;
    private String categoriaProfesional;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String devengoPagaExtra;
    private LocalDate fechaIncorporacion;

}