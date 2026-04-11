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
public class Rutina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    // 🔥 FIX CRÍTICO (IGUAL QUE PLAN)
    @JsonIgnoreProperties({"planes", "rutinas"})
    private Cliente cliente;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rutinaHtml;

    private LocalDate fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDate.now();
    }
}