package com.equipo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "EMPLEADOS")
@Data // Incluye @ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor // Lombok generará un constructor sin argumentos
@AllArgsConstructor // Lombok generará un constructor con todos los argumentos (útil para @Builder)
@Builder // Para usar el patrón builder
public class Empleado {

    @Id
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

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "empleado_especialidades", joinColumns = @JoinColumn(name = "empleado_id"))
    @Column(name = "especialidad")
    @Builder.Default
    private List<String> especialidadesSeleccionadas = new ArrayList<>();

    // PASO 4: Datos económicos
    private String numeroCuenta;
    private String tipoContrato;
    private String categoriaProfesional;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String devengoPagaExtra;
    private LocalDate fechaIncorporacion;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "empleado_etiqueta_join",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @JsonManagedReference
    private Set<Etiqueta> etiquetas = new HashSet<>();

    // Métodos helper para gestionar la relación bidireccional
    public void addEtiqueta(Etiqueta etiqueta) {
        if (this.etiquetas == null) {
            this.etiquetas = new HashSet<>();
        }
        this.etiquetas.add(etiqueta);
        if (etiqueta.getEmpleados() == null) { // Asegurar inicialización en el otro lado
            etiqueta.setEmpleados(new HashSet<>());
        }
        if (!etiqueta.getEmpleados().contains(this)) {
            etiqueta.getEmpleados().add(this);
        }
    }

    public void removeEtiqueta(Etiqueta etiqueta) {
        if (this.etiquetas != null) {
            this.etiquetas.remove(etiqueta);
        }
        if (etiqueta.getEmpleados() != null) {
            etiqueta.getEmpleados().remove(this);
        }
    }
}