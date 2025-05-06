package com.equipo.backend.dto;

import com.equipo.backend.entity.Empleado;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmpleadoRegistroDTO {
    private String nombre;
    private String apellidos;
    private String telefonoMovil;
    private String tipoDocumento;
    private String documento;
    private String departamento;
    private String categoriaProfesional;
    private String tipoContrato;
    private String numeroCuenta;
    private Double salarioBaseMensual;
    private Double complementoMensual;
    private String fechaIncorporacion;

    public Empleado toEntity() {
        Empleado empleado = new Empleado();
        empleado.setNombre(this.nombre + " " + this.apellidos);
        empleado.setTelefono(this.telefonoMovil);
        empleado.setCorreo(this.documento);
        empleado.setBajaLogica(false);
        return empleado;
    }
}