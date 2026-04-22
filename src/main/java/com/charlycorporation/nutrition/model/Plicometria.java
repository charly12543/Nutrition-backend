package com.charlycorporation.nutrition.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plicometria {

    private Double triceps;
    private Double biceps;
    private Double subescapular;
    private Double suprailiaco;
}
