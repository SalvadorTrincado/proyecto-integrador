package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "colaboraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Empleado emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Empleado receptor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoColaboracion estado;

    @Column(nullable = false)
    private LocalDateTime fechaInvitacion;

    private LocalDateTime fechaRespuesta;

    // Nuevos campos opcionales para la cancelación
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancelada_por_id", nullable = true)
    private Empleado canceladaPor;

    @Column(name = "fecha_cancelacion", nullable = true)
    private LocalDateTime fechaCancelacion;

    // Relación con MensajeChat
    @OneToMany(mappedBy = "colaboracion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Evitar recursión en toString con Lombok
    @EqualsAndHashCode.Exclude // Evitar recursión en equals/hashCode con Lombok
    private List<MensajeChat> mensajes = new ArrayList<>();
}