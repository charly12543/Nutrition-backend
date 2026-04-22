package com.charlycorporation.nutrition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"planes", "rutinas"})
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double peso;
    private double altura;
    private int edad;
    private String sexo;
    private String objetivo;
    private String nivel;
    private Integer diasGym;
    private Boolean tomaWhey;

    private LocalDate fechaRegistro;

    // 🔥 NUEVO
    private Boolean tieneMedidas;
    private Boolean tienePlicometria;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Medidas medidas;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Plicometria plicometria;

    // 🔥 RELACIONES
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> planes;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rutina> rutinas;
}



