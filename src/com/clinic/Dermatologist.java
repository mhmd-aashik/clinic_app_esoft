package com.clinic;

import java.time.LocalTime;
import java.util.List;

class Dermatologist {
    private final String name;
    private final List<String> availableDays;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Dermatologist(String name, List<String> availableDays, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.availableDays = availableDays;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() { return name; }
    public List<String> getAvailableDays() { return availableDays; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getSchedule() { return String.join(", ", availableDays); }
}
