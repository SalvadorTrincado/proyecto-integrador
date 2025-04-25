package com.equipo.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
public class DatosContactoDTO {

    @NotBlank(message = "El tipo de documento es obligatorio.")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio.")
    private String documento;

    @NotBlank(message = "El prefijo telefónico es obligatorio.")
    private String prefijoTelefono;

    @NotBlank(message = "El teléfono móvil es obligatorio.")
    @Pattern(regexp = "\\d{9}", message = "El teléfono debe tener 9 dígitos.")
    private String telefonoMovil;

    @NotBlank(message = "El tipo de vía es obligatorio.")
    private String tipoViaDireccionPpal;

    @NotBlank(message = "El nombre de la vía es obligatorio.")
    private String nombreViaDireccionPpal;

    @NotBlank(message = "El número de la vía es obligatorio.")
    @Pattern(regexp = "\\d+", message = "Debe ser un número entero.")
    private String numeroViaDireccionPpal;

    private String portalDireccionPpal;
    private String plantaDireccionPpal;
    private String puertaDireccionPpal;

    @NotBlank(message = "La localidad es obligatoria.")
    private String localidadDireccionPpal;

    private String regionDireccionPpal;

    @NotBlank(message = "El código postal es obligatorio.")
    private String codigoPostalDireccionPpal;
}
