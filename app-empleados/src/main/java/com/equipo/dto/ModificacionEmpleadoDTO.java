package com.equipo.dto;

import com.equipo.entity.Empleado;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ModificacionEmpleadoDTO {

    // --- Datos Personales (del antiguo RegistroEmpleadoPaso1DTO) ---
    @NotBlank(message = "{datosPersonales.nombre.obligatorio}")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "{datosPersonales.apellidos.obligatorio}")
    @Size(min = 2, max = 200, message = "Los apellidos deben tener entre 2 y 200 caracteres")
    private String apellidos;

    private MultipartFile fotografia; // Para la carga de archivos (opcional al modificar)

    @NotBlank(message = "{datosPersonales.genero.obligatorio}")
    private String generoSeleccionado;

    @NotNull(message = "{datosPersonales.fechaNacimiento.obligatoria}")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 16, message = "La edad mínima debe ser 16 años")
    @Max(value = 99, message = "La edad máxima no debe exceder los 99 años")
    private Integer edad;

    @NotBlank(message = "{datosPersonales.paisNacimiento.obligatorio}")
    private String paisNacimiento;

    @Size(max = 500, message = "Los comentarios no pueden exceder los 500 caracteres")
    private String comentarios;

    // --- Datos de Contacto (del antiguo RegistroEmpleadoPaso2DTO) ---
    @NotBlank(message = "{datosContacto.tipoDocumento.obligatorio}")
    private String tipoDocumento;

    @NotBlank(message = "{datosContacto.documento.obligatorio}")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres")
    private String documento; // Generalmente no modificable, pero lo incluimos por completitud del DTO original

    private String prefijoTelefono;

    @NotBlank(message = "{datosContacto.telefonoMovil.obligatorio}")
    private String telefonoMovil;

    @NotBlank(message = "{datosContacto.tipoVia.obligatorio}")
    private String tipoViaDireccionPpal;

    @NotBlank(message = "{datosContacto.nombreVia.obligatorio}")
    @Size(min = 2, max = 200, message = "El nombre de la vía debe tener entre 2 y 200 caracteres")
    private String nombreViaDireccionPpal;

    @NotBlank(message = "{datosContacto.numeroVia.obligatorio}")
    @Size(max = 10, message = "El número de la vía no puede exceder los 10 caracteres")
    private String numeroViaDireccionPpal;

    private String portalDireccionPpal;
    private String plantaDireccionPpal;
    private String puertaDireccionPpal;

    @NotBlank(message = "{datosContacto.localidad.obligatoria}")
    @Size(min = 2, max = 100, message = "La localidad debe tener entre 2 y 100 caracteres")
    private String localidadDireccionPpal;

    @NotBlank(message = "{datosContacto.region.obligatoria}") // Asumiendo que tienes este mensaje
    @Size(min = 2, max = 100, message = "La región debe tener entre 2 y 100 caracteres")
    private String regionDireccionPpal;

    @NotBlank(message = "{datosContacto.codigoPostal.obligatorio}")
    @Pattern(regexp = "^([0-9]{5})$", message = "El formato del código postal no es válido (deben ser 5 dígitos)")
    private String codigoPostalDireccionPpal;

    // Constructor para facilitar la carga desde la entidad Empleado
    public ModificacionEmpleadoDTO(Empleado empleado) {
        this.nombre = empleado.getNombre();
        this.apellidos = empleado.getApellidos();
        // fotografia no se carga aquí, se maneja por separado si se sube una nueva
        this.generoSeleccionado = empleado.getGeneroSeleccionado();
        this.fechaNacimiento = empleado.getFechaNacimiento();
        this.edad = empleado.getEdad();
        this.paisNacimiento = empleado.getPaisNacimiento();
        this.comentarios = empleado.getComentarios();
        this.tipoDocumento = empleado.getTipoDocumento();
        this.documento = empleado.getDocumento();
        this.prefijoTelefono = empleado.getPrefijoTelefono();
        this.telefonoMovil = empleado.getTelefonoMovil();
        this.tipoViaDireccionPpal = empleado.getTipoViaDireccionPpal();
        this.nombreViaDireccionPpal = empleado.getNombreViaDireccionPpal();
        this.numeroViaDireccionPpal = empleado.getNumeroViaDireccionPpal();
        this.portalDireccionPpal = empleado.getPortalDireccionPpal();
        this.plantaDireccionPpal = empleado.getPlantaDireccionPpal();
        this.puertaDireccionPpal = empleado.getPuertaDireccionPpal();
        this.localidadDireccionPpal = empleado.getLocalidadDireccionPpal();
        this.regionDireccionPpal = empleado.getRegionDireccionPpal();
        this.codigoPostalDireccionPpal = empleado.getCodigoPostalDireccionPpal();
    }
}