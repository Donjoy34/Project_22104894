package model;

public class Prescription {
    private String id;
    private String patientId;
    private String medName;
    private String dosage;
    private String instructions;

    public Prescription(String id, String pid, String med, String dose, String instr) {
        this.id = id;
        this.patientId = pid;
        this.medName = med;
        this.dosage = dose;
        this.instructions = instr;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getMedName() { return medName; }

    public String toCSV() {
        return String.join(",", id, patientId, "C001", "A001", "2025-01-01", medName, dosage, "Daily", "7", "1", instructions, "Pharmacy", "Issued", "2025-01-01", "");
    }
}