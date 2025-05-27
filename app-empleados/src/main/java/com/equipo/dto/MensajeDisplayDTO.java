package com.equipo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDisplayDTO {
    private String nombreEmisor;
    private String textoMensaje;
    private String fechaHoraEnvioFormateada;
    private boolean esMio; // Para estilizar en el frontend
}