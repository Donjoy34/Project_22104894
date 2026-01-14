package model;

import util.CSVUtils;
import view.UserRole;
import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DataManager {
    private List<Patient> patients = new ArrayList<>();
    private List<Referral> referrals = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<AuditLog> auditLogs = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();

    private final String DB_PATH = "database/";

    public void loadData() {
        loadPatients();
        loadReferrals();
        loadPrescriptions();
        loadUsers();
        loadAuditLogs();
        loadAppointments();
    }

    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "patients.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 2) {
                    patients.add(new Patient(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(6), data.get(8)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadReferrals() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "referrals.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 9) {
                    referrals.add(new Referral(data.get(0), data.get(1), data.get(2), data.get(7), data.get(9), data.get(11)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadPrescriptions() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "prescriptions.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 10) {
                    prescriptions.add(new Prescription(data.get(0), data.get(1), data.get(5), data.get(6), data.get(10)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "users.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 3) {
                    users.add(new User(data.get(0), data.get(1), data.get(2), UserRole.valueOf(data.get(3))));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadAuditLogs() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "audit_logs.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                AuditLog log = AuditLog.fromCSV(line);
                if (log != null) {
                    auditLogs.add(log);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- Persist Methods ---

    public void addPatient(Patient p) {
        patients.add(p);
        appendToFile("patients.csv", p.toCSV());
    }

    public void addReferral(Referral r) {
        referrals.add(r);
        appendToFile("referrals.csv", r.toCSV());
    }

    public void addPrescription(Prescription p) {
        prescriptions.add(p);
        appendToFile("prescriptions.csv", p.toCSV());
    }

    public void addUser(User u) {
        users.add(u);
        appendToFile("users.csv", u.toCSV());
    }

    public void addAuditLog(AuditLog log) {
        auditLogs.add(log);
        appendToFile("audit_logs.csv", log.toCSV());
    }

    private void appendToFile(String filename, String data) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_PATH + filename, true))) {
            pw.println(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<User> getUsers() { return users; }
    public List<AuditLog> getAuditLogs() { return auditLogs; }

    public User authenticate(String username, String password, UserRole role) {
        String hash = hashPassword(password);
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPasswordHash().equals(hash) && u.getRole() == role) {
                return u;
            }
        }
        return null;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAppointments() {
        // Ensure file exists
        File appointmentFile = new File(DB_PATH + "appointments.csv");
        if (!appointmentFile.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(appointmentFile))) {
                pw.println("appointment_id,patient_id,clinician_id,facility_id,appointment_date,appointment_time,duration_minutes,appointment_type,status,reason_for_visit,notes,created_date,last_modified");
            } catch (IOException e) { e.printStackTrace(); }
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(appointmentFile))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if (data.size() >= 9) {
                    appointments.add(new Appointment(
                        data.get(0).trim(), // id
                        data.get(1).trim(), // patientId
                        data.get(2).trim(), // providerId
                        java.time.LocalDateTime.parse(data.get(4).trim() + " " + data.get(5).trim(), 
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), // appointmentTime
                        data.get(8).trim(), // status
                        data.get(10).trim() // notes
                    ));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void addAppointment(Appointment a) {
        appointments.add(a);
        appendToFile("appointments.csv", a.toCSV());
    }

    public void cancelAppointment(String appointmentId) {
        for (Appointment a : appointments) {
            if (a.getId().equals(appointmentId)) {
                a.setStatus("CANCELLED");
                break;
            }
        }
        // Update CSV
        rewriteAppointmentsCSV();
    }

    public void rewriteAppointmentsCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_PATH + "appointments.csv"))) {
            pw.println("appointment_id,patient_id,clinician_id,facility_id,appointment_date,appointment_time,duration_minutes,appointment_type,status,reason_for_visit,notes,created_date,last_modified");
            for (Appointment a : appointments) {
                pw.println(a.toCSV());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Patient> getPatients() { return patients; }
    public List<Referral> getReferrals() { return referrals; }
    public List<Appointment> getAppointments() { return appointments; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
}