package com.example.hebergement.services;

import com.example.hebergement.entities.Hebergement;
import com.example.hebergement.repositories.HebergementRepository;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.management.Attribute;


@Service
@AllArgsConstructor
public class HebergementService implements IHebergementService {

    private final HebergementRepository hebergementRepository;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;
    private final UserClient userClient;

    @Override
    public Hebergement ajouterHebergement(Hebergement hebergement) {
        return hebergementRepository.save(hebergement);
    }

    @Override
    public Hebergement modifierHebergement(Long id, Hebergement hebergement) {
        Optional<Hebergement> existingHebergement = hebergementRepository.findById(id);
        if (existingHebergement.isPresent()) {
            hebergement.setId(id);
            return hebergementRepository.save(hebergement);
        }
        return null;
    }

    @Override
    public void supprimerHebergement(Long id) {
        hebergementRepository.deleteById(id);
    }

    @Override
    public List<Hebergement> listerHebergements() {
        return hebergementRepository.findAll();
    }

    @Override
    public Hebergement obtenirHebergementParId(Long id) {
        return hebergementRepository.findById(id).orElse(null);
    }

    @Override
    public List<Hebergement> chercherParVille(String ville) {
        return hebergementRepository.findByVille(ville);
    }

    @Override
    public Hebergement assignUserToHebergement(Long userId, Long hebergementId) {
        Hebergement hebergement = hebergementRepository.findById(hebergementId)
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé avec id: " + hebergementId));

        Map<String, Object> user = userClient.getUserById(userId);
        if (user == null || user.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec id: " + userId);
        }

        long nombreUtilisateursActuels = userClient.getUsersByHebergement(hebergementId).size();
        if (nombreUtilisateursActuels >= hebergement.getCapacite()) {
            throw new RuntimeException("Capacité maximale atteinte pour cet hébergement");
        }

        userClient.assignUserToHebergement(userId, hebergementId);

        // Génération du QR Code
        String qrData = "User: " + user.get("username") + "\nEmail: " + user.get("email") +
                "\nHebergement: " + hebergement.getNom() +
                "\nAdresse: " + hebergement.getAdresse();

        String qrCodeBase64;
        try {
            qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code", e);
        }

        // Envoi de l'email
        String email = (String) user.get("email");
        String subject = "Confirmation d'affectation à l'hébergement";
        String body = "Bonjour " + user.get("username") + ",<br><br>Votre affectation à l'hébergement "
                + hebergement.getNom() + " a été validée.<br><br>Veuillez trouver votre QR Code en pièce jointe.";

        try {
            emailService.sendEmailWithQRCode(email, subject, body, qrCodeBase64);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
//Envoi du message WhatsApp à l'utilisateur

        return hebergement;
    }
    @Override
    public Hebergement assignUserToHebergement1(Long userId, Long hebergementId) {
        Hebergement hebergement = hebergementRepository.findById(hebergementId)
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé avec id: " + hebergementId));

        Map<String, Object> user = userClient.getUserById(userId);
        if (user == null || user.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé avec id: " + userId);
        }

        long nombreUtilisateursActuels = userClient.getUsersByHebergement(hebergementId).size();
        if (nombreUtilisateursActuels >= hebergement.getCapacite()) {
            throw new RuntimeException("Capacité maximale atteinte pour cet hébergement");
        }

        userClient.assignUserToHebergement(userId, hebergementId);

        // Génération du QR Code
        String qrData = "User: " + user.get("username") + "\nEmail: " + user.get("email") +
                "\nHebergement: " + hebergement.getNom() +
                "\nAdresse: " + hebergement.getAdresse();

        String qrCodeBase64;
        try {
            qrCodeBase64 = qrCodeService.generateQRCodeBase64(qrData);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR Code", e);
        }

        // Envoi de l'email
        String email = (String) user.get("email");
        String subject = "Confirmation d'affectation à l'hébergement";
        String body = "Bonjour " + user.get("username") + ",<br><br>Votre affectation à l'hébergement "
                + hebergement.getNom() + " a été validée.<br><br>Veuillez trouver votre QR Code en pièce jointe.";

        try {
            emailService.sendEmailWithQRCode(email, subject, body, qrCodeBase64);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }

        return hebergement;
    }


    public BufferedImage createBarChart(Long hebergementId) {
        Map<String, Object> stats = getHebergementStatistics(hebergementId);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue((Number) stats.get("Capacité"), "Capacité", "Capacité");
        dataset.addValue((Number) stats.get("Utilisateurs Actuels"), "Utilisateurs", "Utilisateurs Actuels");
        dataset.addValue((Number) stats.get("Disponibilité"), "Disponibilité", "Disponibilité");

        JFreeChart chart = ChartFactory.createBarChart(
                "Statistiques Hébergement",
                "Catégorie",
                "Valeur",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        renderer.setSeriesPaint(2, Color.GREEN);

        return chart.createBufferedImage(500, 300);
    }

    public Map<String, Object> getHebergementStatistics(Long hebergementId) {
        Hebergement hebergement = hebergementRepository.findById(hebergementId)
                .orElseThrow(() -> new RuntimeException("Hébergement non trouvé avec id: " + hebergementId));

        long nombreUtilisateursActuels = userClient.getUsersByHebergement(hebergementId).size();
        int capacite = hebergement.getCapacite();
        int disponibilite = capacite - (int) nombreUtilisateursActuels;

        Map<String, Object> stats = new HashMap<>();
        stats.put("Capacité", capacite);
        stats.put("Utilisateurs Actuels", nombreUtilisateursActuels);
        stats.put("Disponibilité", disponibilite);

        return stats;
    }

    public ResponseEntity<ByteArrayResource> exportStatisticsToPDF(Long hebergementId) {
        Map<String, Object> stats = getHebergementStatistics(hebergementId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Ajouter un titre
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Statistiques de l'Hébergement", titleFont);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Ajouter l'image du bar chart
            BufferedImage bufferedImage = createBarChart(hebergementId);
            ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", chartOut);
            Image chartImage = Image.getInstance(chartOut.toByteArray());
            chartImage.scaleToFit(500, 300);
            document.add(chartImage);

            document.close();

            // Convertir en ressource téléchargeable
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=hebergement_stats.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(resource);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }


}
