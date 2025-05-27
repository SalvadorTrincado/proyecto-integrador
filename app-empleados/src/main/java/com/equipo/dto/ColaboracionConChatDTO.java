package com.equipo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColaboracionConChatDTO {
    private UUID idColaboracion;
    private String nombreOtroParticipante;
    private String estado; // "ACEPTADA", "CANCELADA", etc.
    private List<MensajeDisplayDTO> mensajes;
    private boolean puedeCancelar;
    private boolean puedeChatear;
}