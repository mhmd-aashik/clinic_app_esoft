package com.clinic;

class Invoice {
    private final Appointment appointment;
    private final TreatmentType treatmentType;
    private final double registrationFee = 500.00;
    private final double taxRate = 0.025;

    public Invoice(Appointment appointment, TreatmentType treatmentType) {
        this.appointment = appointment;
        this.treatmentType = treatmentType;
    }

    public double calculateTotal() {
        double treatmentCost = treatmentType.getPrice();
        double total = registrationFee + treatmentCost;
        double tax = total * taxRate;
        return Math.round((total + tax) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return """
            🏥🧾 Clinic Invoice
            ====================================
            📅 Appointment ID : %d
            🧑 Patient Name   : %s
            💉 Treatment      : %s
            ------------------------------------
            💵 Treatment Cost : LKR %.2f
            🪙 Registration Fee : LKR %.2f
            💰 Tax (2.5%%)      : LKR %.2f
            ====================================
            🧾 Total Amount   : LKR %.2f
            ====================================
            """.formatted(
                appointment.getId(),
                appointment.getPatient().getName(),
                treatmentType.name(),
                treatmentType.getPrice(),
                registrationFee,
                (registrationFee + treatmentType.getPrice()) * taxRate,
                calculateTotal()
        );
    }
}