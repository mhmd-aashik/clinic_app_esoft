package com.clinic;

import java.util.ArrayList;
import java.util.List;

class AppointmentRepository {
    private final List<Appointment> appointments = new ArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findAll() {
        return appointments;
    }

    public List<Appointment> findByDate(String date) {
        List<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDate().equals(date)) {
                filteredAppointments.add(appointment);
            }
        }
        return filteredAppointments;
    }

    public Appointment findById(int id) {
        return appointments.stream().filter(a -> a.getId() == id).findFirst().orElse(null);
    }

    public List<Appointment> search(String query) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getName().equalsIgnoreCase(query) || String.valueOf(appointment.getId()).equals(query)) {
                result.add(appointment);
            }
        }
        return result;
    }
}
