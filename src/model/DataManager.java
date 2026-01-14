package model;

import util.CSVUtils;
import view.UserRole;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataManager {
    private List<Patient> patients = new ArrayList<>();
    private List<Referral> referrals = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<AuditLog> auditLogs = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private List<Staff> staffs = new ArrayList<>();
    private List<Clinician> clinicians = new ArrayList<>();
    
    // User credentials map: username -> (password, role)
    private Map<String, UserCredential> userCredentials = new HashMap<>();

    private String dbPath = "database/";

    public DataManager() {
        Path primary = Paths.get(dbPath);
        if (!Files.exists(primary)) {
            Path alt = Paths.get("src", "database");
            if (Files.exists(alt)) {
                dbPath = alt.toString() + File.separator;
            }
        }
    }

    private String dbPath() {
        return dbPath;
    }

    public void loadData() {
        loadPatients();
        loadReferrals();
        loadPrescriptions();
        loadUsers();
        loadUserCredentials();
        loadAuditLogs();
        loadAppointments();
        loadStaffs();
        loadClinicians();
    }

    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "patients.csv"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "referrals.csv"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "prescriptions.csv"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "users.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // ignore blank rows
                }
                List<String> data = CSVUtils.parseLine(line);
                if (data.size() < 4) {
                    continue; // malformed row
                }
                if ("id".equalsIgnoreCase(data.get(0))) {
                    continue; // skip header even if it appears after blank lines
                }
                try {
                    users.add(new User(data.get(0), data.get(1), data.get(2), UserRole.valueOf(data.get(3))));
                } catch (IllegalArgumentException ex) {
                    // skip invalid role rows
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadAuditLogs() {
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "audit_logs.csv"))) {
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

    public void rewriteUsersCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dbPath() + "users.csv"))) {
            pw.println("id,username,passwordHash,role");
            for (User u : users) {
                pw.println(u.toCSV());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean updateUserRole(String username, UserRole newRole) {
        boolean updated = false;
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                users.set(users.indexOf(u), new User(u.getId(), u.getUsername(), u.getPasswordHash(), newRole));
                updated = true;
                break;
            }
        }
        if (updated) {
            rewriteUsersCSV();
        }
        return updated;
    }

    private void appendToFile(String filename, String data) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(dbPath() + filename, true))) {
            pw.println(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<User> getUsers() { return users; }
    public List<AuditLog> getAuditLogs() { return auditLogs; }

    public User authenticate(String username, String password, UserRole role) {
        if (username != null) username = username.trim();
        if (password != null) password = password.trim();
        // Allow built-in default admin without needing CSV entry
        if (role == UserRole.ADMIN && "admin".equals(username) && "admin".equals(password)) {
            return new User("ADMIN_DEFAULT", "admin", "admin", UserRole.ADMIN);
        }
        // Check user_credentials.csv
        UserCredential cred = userCredentials.get(username);
        if (cred != null && cred.password.equals(password) && cred.role == role) {
            return new User("U_" + username, username, password, role);
        }
        return null;
    }

    // Patient NHS-based authentication
    public Patient authenticatePatientByNHS(String nhsNumber, String password) {
        // Find patient by NHS number
        for (Patient p : patients) {
            if (p.getNhsNumber().equals(nhsNumber)) {
                // Check credentials
                UserCredential cred = userCredentials.get(p.getId());
                if (cred != null && cred.password.equals(password) && cred.role == UserRole.PATIENT) {
                    return p;
                }
            }
        }
        return null;
    }

    // Staff authentication (uses staff ID as username) - supports GP, NURSE, RECEPTIONIST
    public User authenticateStaff(String staffId, String password) {
        UserCredential cred = userCredentials.get(staffId);
        if (cred != null && cred.password.equals(password)) {
            if (cred.role == UserRole.GP || cred.role == UserRole.NURSE || cred.role == UserRole.RECEPTIONIST) {
                return new User("U_" + staffId, staffId, password, cred.role);
            }
        }
        return null;
    }

    // Clinician authentication (uses clinician ID as username)
    public User authenticateClinician(String clinicianId, String password) {
        UserCredential cred = userCredentials.get(clinicianId);
        if (cred != null && cred.password.equals(password) && cred.role == UserRole.SPECIALIST) {
            return new User("U_" + clinicianId, clinicianId, password, UserRole.SPECIALIST);
        }
        return null;
    }

    // GP authentication (GPs are stored as clinicians with GP role)
    public User authenticateGP(String clinicianId, String password) {
        UserCredential cred = userCredentials.get(clinicianId);
        if (cred != null && cred.password.equals(password) && cred.role == UserRole.GP) {
            return new User("U_" + clinicianId, clinicianId, password, UserRole.GP);
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
        return userCredentials.containsKey(patientId);
    }

    // Register patient with NHS number and create password
    public void registerPatientWithPassword(String patientId, String password) {
        userCredentials.put(patientId, new UserCredential(password, UserRole.PATIENT));
        appendToFile("user_credentials.csv", patientId + "," + password + ",PATIENT");
    }

    public String hashPassword(String password) {
        return password; // retained for compatibility; no hashing used
    }

    private void loadAppointments() {
        // Ensure file exists
        File appointmentFile = new File(dbPath() + "appointments.csv");
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
        try (PrintWriter pw = new PrintWriter(new FileWriter(dbPath() + "appointments.csv"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "staff.csv"))) {
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
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "clinicians.csv"))) {
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

    private void loadUserCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader(dbPath() + "user_credentials.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() >= 3) {
                    String username = data.get(0);
                    String password = data.get(1);
                    String roleStr = data.get(2);
                    try {
                        UserRole role = UserRole.valueOf(roleStr);
                        userCredentials.put(username, new UserCredential(password, role));
                    } catch (IllegalArgumentException e) {
                        // Skip invalid role
                    }
                }
            }
        } catch (IOException e) { 
            System.err.println("Warning: user_credentials.csv not found. Authentication may not work.");
        }
    }

    // Inner class for storing credentials
    private static class UserCredential {
        String password;
        UserRole role;
        
        UserCredential(String password, UserRole role) {
            this.password = password;
            this.role = role;
        }
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
        return userCredentials.containsKey(staffId);
    }

    public boolean isClinicianRegistered(String clinicianId) {
        return userCredentials.containsKey(clinicianId);
    }

    public void registerStaffWithPassword(String staffId, String password, UserRole role) {
        userCredentials.put(staffId, new UserCredential(password, role));
        appendToFile("user_credentials.csv", staffId + "," + password + "," + role);
    }
    
    public void registerStaffWithPassword(String staffId, String password) {
        registerStaffWithPassword(staffId, password, UserRole.GP);
    }

    public void registerClinicianWithPassword(String clinicianId, String password) {
        userCredentials.put(clinicianId, new UserCredential(password, UserRole.SPECIALIST));
        appendToFile("user_credentials.csv", clinicianId + "," + password + ",SPECIALIST");
    }

    public void registerGPWithPassword(String gpId, String password) {
        userCredentials.put(gpId, new UserCredential(password, UserRole.GP));
        appendToFile("user_credentials.csv", gpId + "," + password + ",GP");
    }

    // --- Lookup helpers ---
    public Patient findPatientById(String patientId) {
        for (Patient p : patients) {
            if (p.getId().equals(patientId)) {
                return p;
            }
        }
        return null;
    }

    // --- User deletion ---
    public boolean deleteUserByUsername(String username) {
        boolean removed = users.removeIf(u -> u.getUsername().equals(username));
        if (removed) {
            rewriteUsersCSV();
        }
        return removed;
    }

    public boolean usernameExists(String username) {
        return userCredentials.containsKey(username);
    }

    public String generateUserId(String prefix) {
        return prefix + (1000 + users.size());
    }

    public Path backupDatabase() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path backupDir = Paths.get("backups", "backup-" + timestamp);
        try {
            Files.createDirectories(backupDir);
            Path dbDir = Paths.get(dbPath());
            if (Files.exists(dbDir) && Files.isDirectory(dbDir)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbDir)) {
                    for (Path p : stream) {
                        if (Files.isRegularFile(p)) {
                            Files.copy(p, backupDir.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                }
            }
            return backupDir;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}