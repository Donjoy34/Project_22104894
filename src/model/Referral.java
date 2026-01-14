package model;

import util.CSVUtils;

public class Referral {
    private String id;
    private String patientId;
    private String clinicianId;
    private String urgency;
    private String summary;
    private String status;
    private String specialty;
    private String createdDate;

    public Referral(String id, String pId, String cId, String urgency, String summary, String status) {
        this.id = id;
        this.patientId = pId;
        this.clinicianId = cId;
        this.urgency = urgency;
        this.summary = summary;
        this.status = status;
        this.specialty = "General";
        this.createdDate = "2025-01-01";
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getUrgency() { return urgency; }
    public String getSummary() { return summary; }
    public String getStatus() { return status; }
    public String getSpecialty() { return specialty; }
    public String getCreatedDate() { return createdDate; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String toCSV() {
        return String.join(",",
                id, patientId, clinicianId, "RefToC", "RefFac", "RefToFac", "2025-01-01",
                urgency, "Reason", CSVUtils.toCSVField(summary), "Investigations", status, "A000", "Notes", "2025-01-01", "2025-01-01"
        );
    }
}