//AQUI VA TRABAJO ANDRIY

package com.equipo.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EtiquetadoDTO {
    private Long empleadoId;
    private List<String> etiquetasSeleccionadas;
}
