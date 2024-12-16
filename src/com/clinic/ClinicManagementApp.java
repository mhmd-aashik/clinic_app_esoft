package com.clinic;

public class ClinicManagementApp {
    public static void main(String[] args) {
        AppointmentRepository repository = new AppointmentRepository();
        AppointmentService appointmentService = new AppointmentService(repository);
        InvoiceService invoiceService = new InvoiceService(repository);
        ClinicSystem clinicSystem = new ClinicSystem(appointmentService, invoiceService);
        clinicSystem.run();
    }
}
