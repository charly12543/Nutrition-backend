package com.charlycorporation.nutrition.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medidas {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
