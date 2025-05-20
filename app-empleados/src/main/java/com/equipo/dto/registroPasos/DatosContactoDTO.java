package com.equipo.dto.registroPasos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatosContactoDTO {

    @NotBlank(message = "{datosContacto.tipoDocumento.obligatorio}")
    private String tipoDocumento;

    @NotBlank(message = "{datosContacto.documento.obligatorio}")
    private String documento;

    @NotBlank(message = "{datosContacto.prefijoTelefono.obligatorio}")
    private String prefijoTelefono;

    @NotBlank(message = "{datosContacto.telefonoMovil.obligatorio}")
    @Pattern(regexp = "\\d{9}", message = "{datosContacto.telefonoMovil.formato}")
    private String telefonoMovil;

    @NotBlank(message = "{datosContacto.tipoVia.obligatorio}")
    private String tipoVia;

    @NotBlank(message = "{datosContacto.nombreVia.obligatorio}")
    private String nombreVia;

    @NotBlank(message = "{datosContacto.numeroVia.obligatorio}")
    @Pattern(regexp = "\\d+", message = "{datosContacto.numeroVia.formato}")
    private String numeroVia;

    @NotBlank(message = "{datosContacto.localidad.obligatoria}")
    private String localidad;

    @NotBlank(message = "{datosContacto.codigoPostal.obligatorio}")
    private String codigoPostal;
}
