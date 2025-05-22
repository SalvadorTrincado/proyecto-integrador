package com.equipo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroEmpleadoPaso2DTO {

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 5, max = 20, message = "El número de documento debe tener entre 5 y 20 caracteres")
    private String documento;

    private String prefijoTelefono; // No marcado como @NotBlank ya que podría no ser siempre obligatorio

    @NotBlank(message = "El teléfono móvil es obligatorio")
    /*@Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4}$",
            message = "El formato del teléfono móvil no es válido")*/
    private String telefonoMovil;

    @NotBlank(message = "El tipo de vía de la dirección principal es obligatorio")
    private String tipoViaDireccionPpal;

    @NotBlank(message = "El nombre de la vía de la dirección principal es obligatorio")
    @Size(min = 2, max = 200, message = "El nombre de la vía debe tener entre 2 y 200 caracteres")
    private String nombreViaDireccionPpal;

    @NotBlank(message = "El número de la vía de la dirección principal es obligatorio")
    @Size(max = 10, message = "El número de la vía no puede exceder los 10 caracteres")
    private String numeroViaDireccionPpal;

    private String portalDireccionPpal; // No marcado como @NotBlank ya que podría no ser siempre obligatorio
    private String plantaDireccionPpal; // No marcado como @NotBlank ya que podría no ser siempre obligatorio
    private String puertaDireccionPpal; // No marcado como @NotBlank ya que podría no ser siempre obligatorio

    @NotBlank(message = "La localidad de la dirección principal es obligatoria")
    @Size(min = 2, max = 100, message = "La localidad debe tener entre 2 y 100 caracteres")
    private String localidadDireccionPpal;

    @NotBlank(message = "La región de la dirección principal es obligatoria")
    @Size(min = 2, max = 100, message = "La región debe tener entre 2 y 100 caracteres")
    private String regionDireccionPpal;

    @NotBlank(message = "El código postal de la dirección principal es obligatorio")
    @Pattern(regexp = "^([0-9]{5})$", message = "El formato del código postal no es válido (deben ser 5 dígitos)")
    private String codigoPostalDireccionPpal;
}