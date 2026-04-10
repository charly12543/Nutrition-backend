package com.charlycorporation.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"planes", "rutinas"})
    private Cliente cliente;


    @Column(columnDefinition = "JSON")
    private String dieta; // 🔥 antes dietaJson

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String html;

    private Integer calorias;
    private Integer proteina;
    private Integer carbos;
    private Integer grasa;
    private Integer fase;
    private LocalDate fecha;

}