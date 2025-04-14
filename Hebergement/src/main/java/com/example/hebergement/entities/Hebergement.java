package com.example.hebergement.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hebergement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String adresse;
    private String ville;
    private double review;
    private int capacite;

    @Enumerated(EnumType.STRING)
    private TypeHebergement type;

  /*  @ElementCollection
    @JsonProperty("userId")
    @JsonIgnore
    private Map<Integer, Integer> userId = new HashMap<>();
*/

    // Stocker une liste d'utilisateurs sous forme d'une Map (ID utilisateur -> nombre de réservations)
  /*  @ElementCollection
    @JsonProperty("idUsers")
    private Map<Integer, Integer> idusers = new HashMap<>();
*/


}
