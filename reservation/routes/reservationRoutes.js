const express = require("express");
const axios = require("axios");
const Reservation = require("../models/Reservation");
const router = express.Router();

// Base URLs for all microservices
const OFFRE_SERVICE_BASE_URL = process.env.OFFRE_SERVICE_BASE_URL || "http://localhost:8087/offresvoyage";
const VOL_SERVICE_BASE_URL = process.env.VOL_SERVICE_BASE_URL || "http://localhost:8089/api/flights";
const HEBDGEMENT_SERVICE_BASE_URL = process.env.HEBDGEMENT_SERVICE_BASE_URL || "http://localhost:8088/api/hebergements";

router.post("/", async (req, res) => {
  try {
    console.log('Incoming request body:', req.body); // Debug log

    // Validate required fields
    const requiredFields = {
      all: ['itemType', 'travelers', 'personalInfo', 'totalPrice'],
      offre: ['idOffreVoyage'],
      vol: ['idVol'],
      hebergement: ['id']
    };

    // Check common required fields
    for (const field of requiredFields.all) {
      if (!req.body[field]) {
        return res.status(400).json({ error: `Missing required field: ${field}` });
      }
    }

    // Check type-specific required field
    const idField = `id${req.body.itemType.charAt(0).toUpperCase() + req.body.itemType.slice(1)}`;
    if (!req.body[idField]) {
      return res.status(400).json({ error: `Missing ${idField} for ${req.body.itemType} reservation` });
    }

    const itemId = req.body[idField];
    let serviceUrl;

    // Static prices for vol and hebergement
    const staticPrices = {
      vol: 299.99,
      hebergement: 199.99
    };

    // For flights and hebergement, just verify the item exists
    if (req.body.itemType === 'vol' || req.body.itemType === 'hebergement') {
      serviceUrl = req.body.itemType === 'vol' ? VOL_SERVICE_BASE_URL : HEBDGEMENT_SERVICE_BASE_URL;
      await axios.get(`${serviceUrl}/${itemId}`);
    }
    // For offers, fetch the price from microservice
    else if (req.body.itemType === 'offre') {
      serviceUrl = OFFRE_SERVICE_BASE_URL;
      const offerResponse = await axios.get(`${serviceUrl}/${itemId}`);
      if (!offerResponse.data?.prix) {
        return res.status(400).json({ error: "Offer price is missing" });
      }
    }

    // Create reservation - use the totalPrice sent from frontend
    const reservation = new Reservation({
      userId: req.body.userId,
      itemId: itemId.toString(),
      itemType: req.body.itemType,
      travelers: req.body.travelers,
      options: req.body.options || [],
      personalInfo: req.body.personalInfo,
      totalPrice: req.body.totalPrice,
      paymentStatus: req.body.paymentStatus || "pending"
    });

    await reservation.save();
    res.status(201).json(reservation);

  } catch (error) {
    console.error('Reservation error:', {
      url: error.config?.url,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });
    res.status(400).json({ 
      error: error.message,
      details: error.response?.data || 'No additional details'
    });
  }
});

// Get all reservations
router.get("/", async (req, res) => {
  try {
    const reservations = await Reservation.find();
    res.status(200).json(reservations);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get offer details from any microservice
router.get("/:itemType/:itemId", async (req, res) => {
  try {
    const { itemType, itemId } = req.params;

    let serviceUrl;
    switch (itemType) {
      case "offre":
        serviceUrl = OFFRE_SERVICE_BASE_URL;
        break;
      case "vol":
        serviceUrl = VOL_SERVICE_BASE_URL;
        break;
      case "hebergement":
        serviceUrl = HEBDGEMENT_SERVICE_BASE_URL;
        break;
      default:
        return res.status(400).json({ error: "Invalid itemType" });
    }

    const response = await axios.get(`${serviceUrl}/${itemId}`);

    if (!response.data) {
      return res.status(400).json({ error: `${itemType} not found` });
    }

    res.status(200).json(response.data);

  } catch (error) {
    console.error("Error fetching offer details:", error.message);
    res.status(500).json({ error: "Failed to fetch offer details" });
  }
});

// Update a reservation by ID
router.put("/:id", async (req, res) => {
  try {
    const reservation = await Reservation.findByIdAndUpdate(
      req.params.id,
      req.body,
      { new: true, runValidators: true }
    );
    if (!reservation) {
      return res.status(404).json({ error: "Reservation not found" });
    }
    res.status(200).json(reservation);
  } catch (error) {
    res.status(400).json({ error: error.message });
  }
});

// Delete a reservation by ID
router.delete("/:id", async (req, res) => {
  try {
    const reservation = await Reservation.findByIdAndDelete(req.params.id);
    if (!reservation) {
      return res.status(404).json({ error: "Reservation not found" });
    }
    res.status(200).json({ message: "Reservation deleted successfully" });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Get a single reservation by ID
router.get("/:id", async (req, res) => {
  try {
    const reservation = await Reservation.findById(req.params.id);
    if (!reservation) {
      return res.status(404).json({ error: "Reservation not found" });
    }
    res.status(200).json(reservation);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

module.exports = router;