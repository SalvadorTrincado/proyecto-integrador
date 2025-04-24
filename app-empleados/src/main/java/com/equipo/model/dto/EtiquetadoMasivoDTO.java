package com.equipo.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EtiquetadoMasivoDTO {

    private List<Long> empleadosSeleccionados;
    private List<String> etiquetasSeleccionadas;
}
