package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private String id;
    private String patientId;
    private String providerId; // GP or Specialist
    private LocalDateTime appointmentTime;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String notes;

    public Appointment(String id, String patientId, String providerId, LocalDateTime appointmentTime, String status, String notes) {
        this.id = id;
        this.patientId = patientId;
        this.providerId = providerId;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }

    // Getters
    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getProviderId() { return providerId; }
    public LocalDateTime getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return id + "," + patientId + "," + providerId + "," + appointmentTime.format(formatter) + "," + status + "," + notes;
    }
}
