package model;

public class Vitals {
    private String id;
    private String patientId;
    private String nurseName;
    private double bloodPressureSystolic;
    private double bloodPressureDiastolic;
    private double temperature;
    private int heartRate;
    private double weight;
    private String notes;
    private String timestamp;

    public Vitals(String id, String patientId, String nurseName, double bpSys, double bpDias, 
                  double temp, int hr, double weight, String notes, String timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.nurseName = nurseName;
        this.bloodPressureSystolic = bpSys;
        this.bloodPressureDiastolic = bpDias;
        this.temperature = temp;
        this.heartRate = hr;
        this.weight = weight;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    // Getters
    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getNurseName() { return nurseName; }
    public double getBloodPressureSystolic() { return bloodPressureSystolic; }
    public double getBloodPressureDiastolic() { return bloodPressureDiastolic; }
    public double getTemperature() { return temperature; }
    public int getHeartRate() { return heartRate; }
    public double getWeight() { return weight; }
    public String getNotes() { return notes; }
    public String getTimestamp() { return timestamp; }

    public String toCSV() {
        return id + "," + patientId + "," + nurseName + "," + bloodPressureSystolic + "," + bloodPressureDiastolic + 
               "," + temperature + "," + heartRate + "," + weight + "," + notes + "," + timestamp;
    }
}
