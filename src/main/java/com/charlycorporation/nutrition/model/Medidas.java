package com.charlycorporation.nutrition.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medidas {


    private Double cintura;
    private Double pecho;
    private Double espalda;

    private Double brazoDerecho;
    private Double brazoIzquierdo;

    private Double piernaDerecha;
    private Double piernaIzquierda;

    private Double pantorrillaDerecha;
    private Double pantorrillaIzquierda;
}
