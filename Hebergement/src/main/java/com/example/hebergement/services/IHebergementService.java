package com.example.hebergement.services;

import com.example.hebergement.entities.Hebergement;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import java.awt.image.BufferedImage;
import java.util.List;

public interface IHebergementService {
    Hebergement ajouterHebergement(Hebergement hebergement);

    Hebergement modifierHebergement(Long id, Hebergement hebergement);

    void supprimerHebergement(Long id);
    Hebergement assignUserToHebergement1(Long userId, Long hebergementId);
    List<Hebergement> listerHebergements();

    Hebergement obtenirHebergementParId(Long id);

    List<Hebergement> chercherParVille(String ville);
    Hebergement assignUserToHebergement(Long userId, Long hebergementId);
    BufferedImage createBarChart(Long hebergementId);
    ResponseEntity<ByteArrayResource> exportStatisticsToPDF(Long hebergementId);
}