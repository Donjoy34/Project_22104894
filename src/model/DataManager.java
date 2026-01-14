package model;

import util.CSVUtils;
import view.UserRole;
import java.io.*;
import java.util.*;

public class DataManager {
    private List<Patient> patients = new ArrayList<>();
    private List<Referral> referrals = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<AuditLog> auditLogs = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Staff> staffs = new ArrayList<>();
    private List<Clinician> clinicians = new ArrayList<>();

    private final String DB_PATH = "database/";

    public void loadData() {
        loadPatients();
        loadReferrals();
        loadPrescriptions();
        loadUsers();
        loadAuditLogs();
        loadAppointments();
        loadStaffs();
        loadClinicians();
    }

    public void reloadUsers() {
        users.clear();
        loadUsers();
    }    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "patients.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 7) {
                    Patient p = new Patient(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(6), data.get(7));
                    patients.add(p);
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
                if (line.trim().isEmpty()) continue; // Skip empty lines
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 3) {
                    String id = data.get(0).trim();
                    String username = data.get(1).trim();
                    String password = data.get(2).trim();
                    String roleStr = data.get(3).trim();
                    users.add(new User(id, username, password, UserRole.valueOf(roleStr)));
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
            pw.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<User> getUsers() { return users; }
    public List<AuditLog> getAuditLogs() { return auditLogs; }

    public User authenticate(String username, String password, UserRole role) {
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPasswordHash().equals(password) && u.getRole() == role) {
                return u;
            }
        }
        return null;
    }

    // Patient NHS-based authentication
    public Patient authenticatePatientByNHS(String nhsNumber, String password) {
        for (User u : users) {
            if (u.getRole() == UserRole.PATIENT) {
                // Check if this patient's NHS number matches
                for (Patient p : patients) {
                    if (p.getNhsNumber().equals(nhsNumber) && p.getId().equals(u.getUsername())) {
                        if (u.getPasswordHash().equals(password)) {
                            return p;
                        }
                    }
                }
            }
        }
        return null;
    }

    // Check if NHS number exists (for patient self-registration)
    public Patient findPatientByNHS(String nhsNumber) {
        for (Patient p : patients) {
            if (p.getNhsNumber().equals(nhsNumber)) {
                return p;
            }
        }
        return null;
    }

    // Check if patient already has login credentials (has registered)
    public boolean isPatientRegistered(String patientId) {
        for (User u : users) {
            if (u.getUsername().equals(patientId) && u.getRole() == UserRole.PATIENT) {
                return true;
            }
        }
        return false;
    }

    // Register patient with NHS number and create password
    public void registerPatientWithPassword(String patientId, String password) {
        String hash = hashPassword(password);
        User patientUser = new User(patientId, patientId, hash, UserRole.PATIENT);
        addUser(patientUser);
    }

    public String hashPassword(String password) {
        return password;
    }

    public User authenticateStaff(String staffId, String password) {
        // Check if staff ID exists in users.csv with matching password
        for (User u : users) {
            if (u.getUsername().trim().equals(staffId.trim()) && u.getPasswordHash().trim().equals(password.trim())) {
                return u;
            }
        }
        return null;
    }

    public User authenticateClinician(String clinicianId, String password) {
        // Check if clinician ID exists in users.csv with matching password
        for (User u : users) {
            if (u.getUsername().trim().equals(clinicianId.trim()) && u.getPasswordHash().trim().equals(password.trim())) {
                return u;
            }
        }
        return null;
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
    public List<Staff> getStaffs() { return staffs; }
    public List<Clinician> getClinicians() { return clinicians; }

    private void loadStaffs() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "staff.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 11) {
                    Staff s = new Staff(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4),
                                       data.get(5), data.get(6), data.get(7), data.get(8),
                                       data.get(9), data.get(10), data.get(11));
                    staffs.add(s);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadClinicians() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "clinicians.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 11) {
                    Clinician c = new Clinician(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4),
                                               data.get(5), data.get(6), data.get(7), data.get(8),
                                               data.get(9), data.get(10), data.get(11));
                    clinicians.add(c);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void addStaff(Staff s) {
        staffs.add(s);
        appendToFile("staff.csv", s.toCSV());
    }

    public void addClinician(Clinician c) {
        clinicians.add(c);
        appendToFile("clinicians.csv", c.toCSV());
    }

    public Staff findStaffById(String staffId) {
        for (Staff s : staffs) {
            if (s.getId().equals(staffId)) {
                return s;
            }
        }
        return null;
    }

    public Clinician findClinicianById(String clinicianId) {
        for (Clinician c : clinicians) {
            if (c.getId().equals(clinicianId)) {
                return c;
            }
        }
        return null;
    }

    public boolean isStaffRegistered(String staffId) {
        for (User u : users) {
            if (u.getUsername().equals(staffId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isClinicianRegistered(String clinicianId) {
        for (User u : users) {
            if (u.getUsername().equals(clinicianId)) {
                return true;
            }
        }
        return false;
    }

    public void registerStaffWithPassword(String staffId, String password) {
        String userId = "US" + (1000 + users.size());
        User u = new User(userId, staffId, password, UserRole.valueOf("GP")); // Default to GP, can be changed
        users.add(u);
        appendToFile("users.csv", u.toCSV());
        reloadUsers();
    }

    public void registerClinicianWithPassword(String clinicianId, String password) {
        String userId = "UC" + (1000 + users.size());
        User u = new User(userId, clinicianId, password, UserRole.valueOf("SPECIALIST")); // Default to SPECIALIST
        users.add(u);
        appendToFile("users.csv", u.toCSV());
        reloadUsers();
    }

    public void registerReceptionistWithPassword(String staffId, String password) {
        String userId = "UR" + (1000 + users.size());
        User u = new User(userId, staffId, password, UserRole.valueOf("RECEPTIONIST")); // Default to RECEPTIONIST
        users.add(u);
        appendToFile("users.csv", u.toCSV());
        reloadUsers();
    }
}