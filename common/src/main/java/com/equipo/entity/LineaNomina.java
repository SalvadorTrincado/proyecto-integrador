package com.equipo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "lineas_nomina")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaNomina {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nomina_id", nullable = false)
    private Nomina nomina;

    @Column(nullable = false)
    private String concepto;

    @Column(precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoLinea tipo;

    public enum TipoLinea {
        DEVENGOS, DEDUCCIONES
    }
}