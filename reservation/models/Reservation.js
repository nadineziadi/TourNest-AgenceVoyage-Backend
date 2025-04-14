const mongoose = require("mongoose");

const reservationSchema = new mongoose.Schema({
  userId: { type: String, required: true },
  itemId: { type: String, required: true },
  itemType: { type: String, required: true },
  travelers: { type: Number, required: true },
  options: { type: [String], required: true },
  totalPrice: { type: Number, required: true, min: 0 },
  personalInfo: {
    name: { type: String, required: true },
    email: { type: String, required: true },
    phone: { type: String, required: true },
  },
  paymentStatus: { type: String, default: "pending" },
}, { timestamps: true });

module.exports = mongoose.model("Reservation", reservationSchema);
