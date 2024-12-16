package com.clinic;

import java.util.Scanner;

class InvoiceService {
    private final AppointmentRepository repository;
    private final Scanner scanner = new Scanner(System.in);

    public InvoiceService(AppointmentRepository repository) {
        this.repository = repository;
    }

    public boolean generateInvoice(int appointmentId) {
        Appointment appointment = repository.findById(appointmentId);
        if (appointment == null) {
            return false;
        }

        System.out.println("Select Treatment Type:");
        TreatmentType[] treatmentTypes = TreatmentType.values();
        for (int i = 0; i < treatmentTypes.length; i++) {
            System.out.println((i + 1) + ". " + treatmentTypes[i].name() + " (LKR " + treatmentTypes[i].getPrice() + ")");
        }

        try {
            int treatmentChoice = Integer.parseInt(scanner.nextLine()) - 1;
            if (treatmentChoice < 0 || treatmentChoice >= treatmentTypes.length) {
                System.out.println(MessageFormatter.error("Invalid choice."));
                return false;
            }

            TreatmentType treatmentType = treatmentTypes[treatmentChoice];
            Invoice invoice = new Invoice(appointment, treatmentType);
            appointment.markAsPaid();
            System.out.println(invoice);
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid choice."));
        }
        return false;
    }
}
