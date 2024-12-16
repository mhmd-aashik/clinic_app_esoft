package com.clinic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class ClinicSystem {
    private final AppointmentService appointmentService;
    private final InvoiceService invoiceService;
    private final Scanner scanner = new Scanner(System.in);

    public ClinicSystem(AppointmentService appointmentService, InvoiceService invoiceService) {
        this.appointmentService = appointmentService;
        this.invoiceService = invoiceService;
    }

    public void run() {
        while (true) {
            showMenu();
        }
    }

    private void showMenu() {
        System.out.println("\n--- Clinic Management System ---");
        System.out.println(MessageFormatter.info("1. Book Appointment"));
        System.out.println(MessageFormatter.info("2. View Appointments"));
        System.out.println(MessageFormatter.info("3. Search Appointment"));
        System.out.println(MessageFormatter.info("4. View Appointments by Date"));
        System.out.println(MessageFormatter.info("5. Update Appointment"));
        System.out.println(MessageFormatter.info("6. Generate Invoice"));
        System.out.println(MessageFormatter.info("7. Exit"));
        System.out.print(MessageFormatter.prompt("Choose an option: "));

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> bookAppointment();
                case 2 -> viewAppointments();
                case 3 -> searchAppointment();
                case 4 -> viewAppointmentsByDate();
                case 5 -> updateAppointment();
                case 6 -> generateInvoice();
                case 7 -> {
                    System.out.println("Exiting system. Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println(MessageFormatter.error("Invalid choice. Please try again."));
            }
        } catch (NumberFormatException e) {
            System.out.println(MessageFormatter.error("Invalid input. Please enter a number."));
        }
    }

    // 1. Booking an appointment
    private void bookAppointment() {
        String nic = promptInput("Enter NIC (9 or 12 characters; old format: 9 digits + V/X or new format: 12 digits): ",
                InputValidator::isValidNic);

        String name = promptInput("Enter Name (minimum 4 characters): ", InputValidator::isValidName);

        String email = promptInput("Enter Email Address (e.g., example@example.com): ", InputValidator::isValidEmail);

        String phone = promptInput("Enter Phone Number (10 digits): ", InputValidator::isValidPhone);

        Patient patient = new Patient(nic, name, email, phone);

        // Display available dermatologists
        List<Dermatologist> dermatologists = appointmentService.getDermatologists();
        if (dermatologists.isEmpty()) {
            System.out.println(MessageFormatter.error("No dermatologists available. Please try again later."));
            return;
        }

        System.out.println(MessageFormatter.info("Available Dermatologists:"));
        for (int i = 0; i < dermatologists.size(); i++) {
            System.out.println((i + 1) + ". " + dermatologists.get(i).getName() + " (Available: " + dermatologists.get(i).getSchedule() + ")");
        }

        int doctorChoice = promptNumericInput("Select a dermatologist (enter number): ", 1, dermatologists.size()) - 1;
        Dermatologist selectedDoctor = dermatologists.get(doctorChoice);

        // Display available dates
        List<LocalDate> availableDates = generateNextAvailableDates(selectedDoctor.getAvailableDays(), 5);
        if (availableDates.isEmpty()) {
            System.out.println(MessageFormatter.error("No available dates for the selected dermatologist."));
            return;
        }

        System.out.println(MessageFormatter.info("Available Dates:"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(dateFormatter));
        }

        int dateChoice = promptNumericInput("Select an available date: ", 1, availableDates.size()) - 1;
        LocalDate appointmentDate = availableDates.get(dateChoice);

        // Display available time slots
        List<LocalTime> availableTimes = generateTimeSlots(selectedDoctor.getStartTime(), selectedDoctor.getEndTime(), 15);
        if (availableTimes.isEmpty()) {
            System.out.println(MessageFormatter.error("No available time slots for the selected date."));
            return;
        }

        System.out.println(MessageFormatter.info("Available Time Slots:"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i).format(timeFormatter));
        }

        int timeChoice = promptNumericInput("Select an available time slot: ", 1, availableTimes.size()) - 1;
        LocalTime appointmentTime = availableTimes.get(timeChoice);

        // **Check for Conflicts**
        boolean isConflict = appointmentService.isAppointmentConflict(selectedDoctor, appointmentDate, appointmentTime);
        if (isConflict) {
            System.out.println(MessageFormatter.error("The selected time slot is already booked for this dermatologist on the chosen date."));
            return;
        }

        System.out.println(MessageFormatter.info("The registration fee is LKR 500.00. Confirm payment? (yes/no)"));
        String confirmation = scanner.nextLine();
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println(MessageFormatter.error("Registration fee payment not confirmed. Appointment not booked."));
            return;
        }

        // Finalize and book appointment
        Appointment appointment = new Appointment(patient, selectedDoctor, appointmentDate.format(dateFormatter), appointmentTime.format(timeFormatter));
        appointmentService.bookAppointment(appointment);
        System.out.println(MessageFormatter.success("Appointment successfully booked!"));
    }
    // 2. View all appointments
    private void viewAppointments() {
        System.out.println(MessageFormatter.info("List of Appointments:"));
        appointmentService.viewAppointments().forEach(System.out::println);
    }

    // 3. Search for an appointment
    private void searchAppointment() {
        System.out.print(MessageFormatter.prompt("Enter Patient Name or Appointment ID to search: "));
        String query = scanner.nextLine();
        List<Appointment> results = appointmentService.searchAppointment(query);

        if (results.isEmpty()) {
            System.out.println(MessageFormatter.error("No appointments found for the given query."));
        } else {
            results.forEach(System.out::println);
        }
    }

    // 4. View appointments by date
    private void viewAppointmentsByDate() {
        System.out.println(MessageFormatter.info("Select a Year:"));
        int year = promptYear();

        System.out.println(MessageFormatter.info("Select a Month:"));
        int month = promptMonth();

        System.out.println(MessageFormatter.info("Select a Date:"));
        int day = promptDay(year, month);

        LocalDate selectedDate = LocalDate.of(year, month, day);
        String formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Appointment> appointments = appointmentService.getAppointmentsByDate(formattedDate);
        if (appointments.isEmpty()) {
            System.out.println(MessageFormatter.info("No appointments found for " + formattedDate + "."));
        } else {
            System.out.println(MessageFormatter.info("Appointments on " + formattedDate + ":"));
            appointments.forEach(System.out::println);
        }
    }

    private int promptYear() {
        return promptNumericInput("Enter year (2024 or 2025): ", 2024, 2025);
    }

    private int promptMonth() {
        return promptNumericInput("Enter month (1 - 12): ", 1, 12);
    }

    private int promptDay(int year, int month) {
        int maxDays = LocalDate.of(year, month, 1).lengthOfMonth();
        return promptNumericInput("Enter day (1 - " + maxDays + "): ", 1, maxDays);
    }

    private int promptNumericInput(String message, int min, int max) {
        int input;
        while (true) {
            System.out.print(MessageFormatter.prompt(message));
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println(MessageFormatter.error("Invalid input. Please try again."));
        }
    }

    private String promptInput(String message, Validator validator) {
        String input;
        do {
            System.out.print(MessageFormatter.prompt(message));
            input = scanner.nextLine();
        } while (!validator.validate(input));
        return input;
    }

    private List<LocalDate> generateNextAvailableDates(List<String> availableDays, int limit) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        int count = 0;

        while (count < limit) {
            for (String day : availableDays) {
                LocalDate nextDate = startDate.with(java.time.DayOfWeek.valueOf(day.toUpperCase()));
                if (!nextDate.isBefore(startDate)) {
                    dates.add(nextDate);
                    count++;
                    if (count >= limit) break;
                }
            }
            startDate = startDate.plusWeeks(1);
        }
        return dates;
    }

    private List<LocalTime> generateTimeSlots(LocalTime startTime, LocalTime endTime, int intervalMinutes) {
        List<LocalTime> timeSlots = new ArrayList<>();
        LocalTime time = startTime;

        while (time.isBefore(endTime)) {
            timeSlots.add(time);
            time = time.plusMinutes(intervalMinutes);
        }
        return timeSlots;
    }

    // 5. Update an appointment
    private void updateAppointment() {
        int appointmentId = promptNumericInput("Enter Appointment ID to update: ", 1, Integer.MAX_VALUE);
        if (!appointmentService.updateAppointment(appointmentId, scanner)) {
             if(appointmentService.updateAppointment(appointmentId, scanner)){
                 System.out.println(MessageFormatter.error("No appointment found with the given ID. Update failed."));
             }
        } else {
            System.out.println(MessageFormatter.success("Appointment updated successfully."));
        }
    }

    // 6. Generate an invoice
    private void generateInvoice() {
        int appointmentId = promptNumericInput("Enter Appointment ID to generate invoice: ", 1, Integer.MAX_VALUE);
        if (!invoiceService.generateInvoice(appointmentId)) {
            if(invoiceService.generateInvoice(appointmentId)){
                System.out.println(MessageFormatter.error("No appointment found with the given ID. Invoice generation failed."));
            }
        } else {
            System.out.println(MessageFormatter.success("Invoice generated successfully."));
        }
    }
}