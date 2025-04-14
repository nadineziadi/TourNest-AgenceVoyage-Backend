package com.example.hebergement.controllers;

import com.example.hebergement.entities.Hebergement;
import com.example.hebergement.services.IHebergementService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/hebergements")
public class HebergementController {

    @Autowired
    private IHebergementService hebergementService;

    @PostMapping("/addhebergement")
    public Hebergement ajouter(@RequestBody Hebergement hebergement) {
        return hebergementService.ajouterHebergement(hebergement);
    }

    @PutMapping("/updatehebergement/{id}")
    public Hebergement modifier(@PathVariable Long id, @RequestBody Hebergement hebergement) {
        return hebergementService.modifierHebergement(id, hebergement);
    }

    @DeleteMapping("/deletehebergement/{id}")
    public void supprimer(@PathVariable Long id) {
        hebergementService.supprimerHebergement(id);
    }

    @GetMapping("/getallhebergement")
    public List<Hebergement> lister() {
        return hebergementService.listerHebergements();
    }

    @GetMapping("/gethebergementby/{id}")
    public Hebergement obtenirParId(@PathVariable Long id) {
        return hebergementService.obtenirHebergementParId(id);
    }

    @GetMapping("/{id}")
    public Hebergement obtenirParId1(@PathVariable Long id) {
        return hebergementService.obtenirHebergementParId(id);
    }

    @GetMapping("/getbyville/{ville}")
    public List<Hebergement> chercherParVille(@PathVariable String ville) {
        return hebergementService.chercherParVille(ville);
    }



    @Operation(summary = "Assignation de l'utilisateur à un hébergement")
    @PostMapping("/{hebergementId}/assignUser/{userId}")
    public ResponseEntity<Hebergement> assignUserToHebergement(
            @PathVariable("hebergementId") Long hebergementId,
            @PathVariable("userId") Long userId) {


        Hebergement hebergement = hebergementService.assignUserToHebergement(userId, hebergementId);
        return ResponseEntity.ok(hebergement);

    }
    @Operation(summary = "Assignation de l'utilisateur à un hébergement")
    @PostMapping("/{hebergementId}/assignUser1/{userId}")
    public ResponseEntity<Hebergement> assignUserToHebergement1(
            @PathVariable("hebergementId") Long hebergementId,
            @PathVariable("userId") Long userId) {

        try {
            Hebergement hebergement = hebergementService.assignUserToHebergement1(userId, hebergementId);
            return ResponseEntity.ok(hebergement);
        } catch (RuntimeException e) {
            // Gestion des erreurs
            return ResponseEntity.status(400).body(null);
        }
    }
    @Operation(summary = "Exportation des statistiques de l'hébergement au format PDF")
    @GetMapping("/{hebergementId}/exportStatsToPDF")
    public ResponseEntity<ByteArrayResource> exportStatisticsToPDF(@PathVariable("hebergementId") Long hebergementId) {
        try {
            return hebergementService.exportStatisticsToPDF(hebergementId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
