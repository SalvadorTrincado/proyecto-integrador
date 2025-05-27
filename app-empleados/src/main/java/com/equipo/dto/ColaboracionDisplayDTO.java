package com.equipo.dto;

import com.equipo.entity.EstadoColaboracion;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ColaboracionDisplayDTO {
    private UUID id;
    private String emisorNombreCompleto;
    private String emisorEmail;
    private UUID emisorId;
    private String receptorNombreCompleto;
    private String receptorEmail;
    private UUID receptorId;
    private EstadoColaboracion estado;
    private LocalDateTime fechaInvitacion;
    private LocalDateTime fechaRespuesta;
    private boolean puedeChatear;
    private boolean puedeCancelar;
    private LocalDateTime fechaCancelacion;
    private String canceladaPorNombre;
}