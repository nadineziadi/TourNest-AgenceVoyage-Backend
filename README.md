Parfait ! Voici un **document formel (style rapport)** uniquement centré sur les **fonctionnalités** et les **technologies utilisées**, prêt à être remis à ton professeur. Tu peux le copier dans un fichier `.docx` ou `.pdf`.

---

# 📄 **Document de Présentation du Projet – Trip Agency**

## 📝 **1. Fonctionnalités du Système**

Le projet **Trip Agency** est une application web distribuée destinée à la gestion complète d'une agence de voyage. Il offre les fonctionnalités suivantes :

### 🔹 Gestion des utilisateurs
- Création, modification, suppression de comptes.
- Authentification via login et mot de passe (sécurisé avec JWT).
- Attribution de rôles (`ADMIN`, `CLIENT`).

### 🔹 Gestion des vols
- Consultation des vols par date, destination, prix.
- Réservation et annulation de billets.
- Suivi des statuts de réservation.

### 🔹 Gestion des hébergements
- Ajout, mise à jour, suppression d’hôtels, maisons d’hôtes, etc.
- Association des hébergements aux offres de voyage.
- Affichage des disponibilités.

### 🔹 Gestion des offres de voyages
- Création d’offres combinées (vol + hébergement).
- Consultation et filtrage des offres.
- Réservation complète depuis une seule interface.

### 🔹 Gestion des réductions et promotions
- Création et application de promotions personnalisées.
- Offres dynamiques selon critères (saisons, destinations, clients fidèles).

### 🔹 Suggestions & Feedback utilisateur
- Système de recommandation (basé sur l’historique).
- Collecte d’avis et de notes via MongoDB.

---

## ⚙️ **2. Technologies Utilisées**

Le système est basé sur une **architecture microservices** conteneurisée avec **Docker**.

### 🔧 Backend
- **Spring Boot** (Java) : pour les microservices de gestion (vol, user, hébergement, offre, réduction).
- **Node.js + Express** : pour le service de recommandations/suggestions.
- **Spring Cloud Eureka** : pour la découverte de services.
- **Spring Cloud Gateway** : pour la gestion centralisée des appels API.

### 💻 Frontend
- **Angular** : interface utilisateur moderne, responsive.
- **Tailwind CSS** & **Bootstrap** : pour le design.

### 🗄️ Base de données
- **MySQL** : pour les services vol, user, hébergement, offres.
- **MongoDB** : pour les avis, suggestions et données non structurées.

### 🔐 Sécurité
- **JWT (JSON Web Token)** : pour l’authentification et la sécurité.
- **bcrypt** : pour le hachage des mots de passe.

### 🐳 Conteneurisation
- **Docker** : conteneurisation de chaque microservice.
- **Docker Compose** : orchestration multi-services pour faciliter le déploiement.

---

## 🧪 Environnement de Déploiement

- Tous les services sont accessibles via une **API Gateway** sur `localhost:8089`.
- Les microservices sont automatiquement découverts via **Eureka** (`localhost:8761`).
- L’interface Angular est accessible sur `localhost:4200`.

---

## ✅ Conclusion

Ce projet répond aux besoins d’une agence de voyage moderne grâce à :
- Une architecture distribuée et modulaire.
- Une interface utilisateur claire et intuitive.
- Une intégration de technologies modernes assurant performance, sécurité et extensibilité.

---

Souhaite-tu que je te le convertisse en **document Word ou PDF prêt à remettre** ?
