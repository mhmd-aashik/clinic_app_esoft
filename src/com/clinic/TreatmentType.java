package com.clinic;

enum TreatmentType {
    ACNE(2750.00),
    SKIN_WHITENING(7650.00),
    MOLE_REMOVAL(3850.00),
    LASER_TREATMENT(12500.00);

    private final double price;
    TreatmentType(double price) { this.price = price; }
    public double getPrice() { return price; }
}