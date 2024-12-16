package com.clinic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class AppointmentService {
    private final AppointmentRepository repository;
    private final List<Dermatologist> dermatologists;

    public AppointmentService(AppointmentRepository repository) {
        this.repository = repository;
        this.dermatologists = List.of(
                new Dermatologist("Dr. Silva", List.of("MONDAY", "WEDNESDAY", "FRIDAY"), LocalTime.of(9, 0), LocalTime.of(17, 0)),
                new Dermatologist("Dr. Perera", List.of("SATURDAY"), LocalTime.of(10, 0), LocalTime.of(15, 0)),
                new Dermatologist("Dr. Fernando", List.of("TUESDAY", "THURSDAY", "SATURDAY"), LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new Dermatologist("Dr. Wijeratne", List.of("MONDAY", "THURSDAY", "SUNDAY"), LocalTime.of(13, 0), LocalTime.of(18, 0))
        );
    }

    public List<Dermatologist> getDermatologists() {
        return dermatologists;
    }

    public void bookAppointment(Appointment appointment) {
        repository.save(appointment);
        System.out.println(appointment);
    }




    public List<Appointment> viewAppointments() {
        return repository.findAll();
    }

    public List<Appointment> searchAppointment(String query) {
        return repository.search(query);
    }

    // New method to get appointments by date
    public List<Appointment> getAppointmentsByDate(String date) {
        return repository.findByDate(date);
    }

    public boolean updateAppointment(int appointmentId, Scanner scanner) {
        Appointment appointment = repository.findById(appointmentId);
        if (appointment == null) {
            return false;
        }

        System.out.println(MessageFormatter.info("Updating Appointment Details for Appointment ID: " + appointmentId));
        System.out.println("Current Details: " + appointment);

        // Ask the user what they want to update
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Dermatologist");
        System.out.println("2. Appointment Date");
        System.out.println("3. Appointment Time");
        System.out.println("4. Cancel Update");
        int choice = promptNumericInput("Choose an option: ", 1, 4, scanner);

        switch (choice) {
            case 1 -> updateDermatologist(appointment, scanner);
            case 2 -> updateAppointmentDate(appointment, scanner);
            case 3 -> updateAppointmentTime(appointment, scanner);
            case 4 -> {
                System.out.println(MessageFormatter.info("Update cancelled."));
                return false;
            }
        }

        // Save the updated appointment
        repository.save(appointment);
        System.out.println(MessageFormatter.success("Appointment updated successfully!"));
        return false;
    }

    private void updateDermatologist(Appointment appointment, Scanner scanner) {
        System.out.println(MessageFormatter.info("Available Dermatologists:"));
        for (int i = 0; i < dermatologists.size(); i++) {
            System.out.println((i + 1) + ". " + dermatologists.get(i).getName() + " (Available: " + dermatologists.get(i).getSchedule() + ")");
        }

        int doctorChoice = promptNumericInput("Select a dermatologist (enter number): ", 1, dermatologists.size(), scanner) - 1;
        Dermatologist selectedDoctor = dermatologists.get(doctorChoice);
        appointment.setDermatologist(selectedDoctor);
        System.out.println(MessageFormatter.success("Dermatologist updated to: " + selectedDoctor.getName()));
    }

    private void updateAppointmentDate(Appointment appointment, Scanner scanner) {
        Dermatologist doctor = appointment.getDermatologist();
        List<LocalDate> availableDates = generateNextAvailableDates(doctor.getAvailableDays(), 5);

        if (availableDates.isEmpty()) {
            System.out.println(MessageFormatter.error("No available dates for the selected dermatologist."));
            return;
        }

        System.out.println(MessageFormatter.info("Available Dates:"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < availableDates.size(); i++) {
            System.out.println((i + 1) + ". " + availableDates.get(i).format(dateFormatter));
        }

        int dateChoice = promptNumericInput("Select an available date by entering the corresponding number: ", 1, availableDates.size(), scanner) - 1;
        LocalDate newDate = availableDates.get(dateChoice);
        appointment.setDate(newDate.format(dateFormatter));
        System.out.println(MessageFormatter.success("Appointment date updated to: " + newDate));
    }

    private void updateAppointmentTime(Appointment appointment, Scanner scanner) {
        Dermatologist doctor = appointment.getDermatologist();
        List<LocalTime> availableTimes = generateTimeSlots(doctor.getStartTime(), doctor.getEndTime(), 15);

        if (availableTimes.isEmpty()) {
            System.out.println(MessageFormatter.error("No available time slots for the selected dermatologist."));
            return;
        }

        System.out.println(MessageFormatter.info("Available Time Slots:"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (int i = 0; i < availableTimes.size(); i++) {
            System.out.println((i + 1) + ". " + availableTimes.get(i).format(timeFormatter));
        }

        int timeChoice = promptNumericInput("Select an available time slot by entering the corresponding number: ", 1, availableTimes.size(), scanner) - 1;
        LocalTime newTime = availableTimes.get(timeChoice);
        appointment.setTime(newTime.format(timeFormatter));
        System.out.println(MessageFormatter.success("Appointment time updated to: " + newTime));
    }

    private int promptNumericInput(String message, int min, int max, Scanner scanner) {
        int input;
        while (true) {
            System.out.print(MessageFormatter.prompt(message));
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) break;
            } catch (NumberFormatException ignored) {}
            System.out.println(MessageFormatter.error("Invalid choice. Please try again."));
        }
        return input;
    }

    private List<LocalDate> generateNextAvailableDates(List<String> daysOfWeek, int numberOfDates) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        int count = 0;

        while (count < numberOfDates) {
            for (String day : daysOfWeek) {
                try {
                    java.time.DayOfWeek dayOfWeek = java.time.DayOfWeek.valueOf(day.toUpperCase());
                    LocalDate nextDate = currentDate.with(java.time.temporal.TemporalAdjusters.nextOrSame(dayOfWeek));
                    if (!dates.contains(nextDate)) {
                        dates.add(nextDate);
                        count++;
                        if (count >= numberOfDates) break;
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(MessageFormatter.error("Invalid day of the week: " + day));
                }
            }
            currentDate = currentDate.plusWeeks(1); // Advance by a week if needed
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

    public boolean isAppointmentConflict(Dermatologist selectedDoctor, LocalDate appointmentDate, LocalTime appointmentTime) {
        return false;
    }
}