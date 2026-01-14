package view;

import model.DataManager;
import model.User;
import model.Patient;
import model.Staff;
import model.Clinician;
import model.AuditLog;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class LoginView extends JFrame {
    private UserRole role;
    private MainView mainView;
    private DataManager model;

    public LoginView(UserRole role, MainView mainView, DataManager model) {
        this.role = role;
        this.mainView = mainView;
        this.model = model;
        model.loadData(); // Load all data from CSV files
        setTitle("Login - " + role);
        setSize(350, 280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // For PATIENT role, use NHS number; for others use username
        if (role == UserRole.PATIENT) {
            contentPanel.add(new JLabel("NHS Number:"));
        } else {
            contentPanel.add(new JLabel("Username:"));
        }
        JTextField txtUsername = new JTextField();
        contentPanel.add(txtUsername);

        contentPanel.add(new JLabel("Password:"));
        JPasswordField txtPassword = new JPasswordField();

        if (role == UserRole.ADMIN) {
            txtUsername.setText("admin");
            txtPassword.setText("admin");
        }

        contentPanel.add(txtPassword);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            
            if (role == UserRole.PATIENT) {
                // Patient NHS-based login - check user_credentials.csv
                Patient patient = model.findPatientByNHS(username);
                if (patient != null) {
                    // Check if patient is registered in user_credentials.csv
                    Patient authenticatedPatient = model.authenticatePatientByNHS(username, password);
                    if (authenticatedPatient != null) {
                        User patientUser = new User(patient.getId(), patient.getId(), password, UserRole.PATIENT);
                        JOptionPane.showMessageDialog(this, "Login successful!");
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), patient.getId(), "LOGIN", LocalDateTime.now(), "Patient logged in with NHS: " + username);
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(patientUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        // Patient exists but not registered or wrong password
                        if (!model.isPatientRegistered(patient.getId())) {
                            int choice = JOptionPane.showConfirmDialog(this, 
                                "Patient found but not registered. Would you like to register now?", 
                                "Not Registered", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                openPatientSelfRegistration();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid password");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "NHS number not found. Please check with reception.");
                }
            } else if (role == UserRole.GP) {
                // GP login using clinician_id and password (GPs are stored as Clinicians) - check user_credentials.csv
                Clinician gp = model.findClinicianById(username);
                if (gp != null) {
                    // Check if GP has registered in user_credentials.csv
                    User gpUser = model.authenticateGP(username, password);
                    if (gpUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome Dr. " + gp.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), gp.getId(), "LOGIN", LocalDateTime.now(), "GP logged in: Dr. " + gp.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(gpUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        // GP exists but not registered or wrong password
                        if (!model.isClinicianRegistered(username)) {
                            int choice = JOptionPane.showConfirmDialog(this, 
                                "Clinician ID found but not registered. Would you like to register now?", 
                                "Not Registered", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                openStandardRegistration();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid password");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Clinician ID not found. Please check with Admin.");
                }
            } else if (role == UserRole.NURSE || role == UserRole.RECEPTIONIST) {
                // Staff login using staff_id and password - check user_credentials.csv
                Staff staff = model.findStaffById(username);
                if (staff != null) {
                    // Check if staff has registered in user_credentials.csv
                    User staffUser = model.authenticateStaff(username, password);
                    if (staffUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome " + staff.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), staff.getId(), "LOGIN", LocalDateTime.now(), "Staff logged in: " + staff.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(staffUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        // Staff exists but not registered or wrong password
                        if (!model.isStaffRegistered(username)) {
                            int choice = JOptionPane.showConfirmDialog(this, 
                                "Staff ID found but not registered. Would you like to register now?", 
                                "Not Registered", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                openStandardRegistration();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid password");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Staff ID not found. Please check with Admin.");
                }
            } else if (role == UserRole.SPECIALIST) {
                // Clinician login using clinician_id and password - check user_credentials.csv
                Clinician clinician = model.findClinicianById(username);
                if (clinician != null) {
                    // Check if clinician has registered in user_credentials.csv
                    User clinUser = model.authenticateClinician(username, password);
                    if (clinUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome Dr. " + clinician.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), clinician.getId(), "LOGIN", LocalDateTime.now(), "Clinician logged in: Dr. " + clinician.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(clinUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        // Clinician exists but not registered or wrong password
                        if (!model.isClinicianRegistered(username)) {
                            int choice = JOptionPane.showConfirmDialog(this, 
                                "Clinician ID found but not registered. Would you like to register now?", 
                                "Not Registered", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                openStandardRegistration();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid password");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Clinician ID not found. Please check with Admin.");
                }
            } else {
                // Standard authentication for other roles
                if (role == UserRole.ADMIN && "admin".equals(username) && "admin".equals(password)) {
                    User adminUser = new User("ADMIN_DEFAULT", "admin", "admin", UserRole.ADMIN);
                    JOptionPane.showMessageDialog(this, "Login successful for ADMIN");
                    AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), adminUser.getId(), "LOGIN", LocalDateTime.now(), "Default admin logged in");
                    model.addAuditLog(loginLog);
                    mainView.setCurrentUser(adminUser);
                    dispose();
                    mainView.switchToDashboard();
                    return;
                }

                User user = model.authenticate(username, password, role);
                if (user != null) {
                    JOptionPane.showMessageDialog(this, "Login successful for " + role);
                    AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), user.getId(), "LOGIN", LocalDateTime.now(), "User logged in");
                    model.addAuditLog(loginLog);
                    mainView.setCurrentUser(user);
                    dispose();
                    mainView.switchToDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch");
                }
            }
        });
        buttonPanel.add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(e -> openRegisterDialog());
        buttonPanel.add(btnRegister);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void openRegisterDialog() {
        if (role == UserRole.PATIENT) {
            openPatientSelfRegistration();
        } else {
            openStandardRegistration();
        }
    }

    // Patient self-registration using NHS number
    private void openPatientSelfRegistration() {
        JDialog registerDialog = new JDialog(this, "Patient Registration", true);
        registerDialog.setSize(350, 220);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        contentPanel.add(new JLabel("NHS Number:"));
        JTextField txtNHS = new JTextField();
        contentPanel.add(txtNHS);

        contentPanel.add(new JLabel("Create Password:"));
        JPasswordField txtPassword = new JPasswordField();
        contentPanel.add(txtPassword);

        contentPanel.add(new JLabel("Confirm Password:"));
        JPasswordField txtConfirm = new JPasswordField();
        contentPanel.add(txtConfirm);

        registerDialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnSubmit = new JButton("Register");
        btnSubmit.addActionListener(e -> {
            String nhs = txtNHS.getText();
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            // Find patient by NHS number
            Patient patient = model.findPatientByNHS(nhs);
            if (patient == null) {
                JOptionPane.showMessageDialog(registerDialog, "NHS number not found. Please check with reception.");
                return;
            }

            if (!password.equals(confirm) || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match or are empty");
                return;
            }

            if (model.isPatientRegistered(patient.getId())) {
                JOptionPane.showMessageDialog(registerDialog, "Patient already registered. Please login instead.");
                return;
            }

            // Register patient with password
            model.registerPatientWithPassword(patient.getId(), password);
            AuditLog regLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), patient.getId(), "REGISTER", LocalDateTime.now(), "Patient self-registered with NHS: " + nhs);
            model.addAuditLog(regLog);
            
            JOptionPane.showMessageDialog(registerDialog, "Registration successful! You can now login with your NHS number and password.");
            registerDialog.dispose();
        });
        buttonPanel.add(btnSubmit);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> registerDialog.dispose());
        buttonPanel.add(btnCancel);

        registerDialog.add(buttonPanel, BorderLayout.SOUTH);
        registerDialog.setVisible(true);
    }

    // Standard registration for non-patient roles
    private void openStandardRegistration() {
        JDialog registerDialog = new JDialog(this, "Register", true);
        registerDialog.setSize(300, 250);
        registerDialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String labelText = role.toString().charAt(0) + role.toString().substring(1).toLowerCase();
        if (role.toString().equals("SPECIALIST")) {
            labelText = "Clinician";
        }
        panel.add(new JLabel(labelText + " ID:"));
        JTextField txtRegUsername = new JTextField();
        panel.add(txtRegUsername);

        panel.add(new JLabel("Password:"));
        JPasswordField txtRegPassword = new JPasswordField();
        panel.add(txtRegPassword);

        panel.add(new JLabel("Confirm Password:"));
        JPasswordField txtRegConfirm = new JPasswordField();
        panel.add(txtRegConfirm);

        JButton btnRegSubmit = new JButton("Register");
        btnRegSubmit.addActionListener(e -> {
            String id = txtRegUsername.getText();
            String password = new String(txtRegPassword.getPassword());
            String confirm = new String(txtRegConfirm.getPassword());
            
            if (!password.equals(confirm) || password.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match or are empty");
                return;
            }

            // Check if ID exists and is not already registered
            if (role == UserRole.GP) {
                // GP registration
                Clinician gp = model.findClinicianById(id);
                if (gp == null) {
                    JOptionPane.showMessageDialog(registerDialog, "GP ID not found. Please check with Admin.");
                    return;
                }
                if (model.isClinicianRegistered(id)) {
                    JOptionPane.showMessageDialog(registerDialog, "GP already registered. Please login instead.");
                    return;
                }
                model.registerGPWithPassword(id, password);
                JOptionPane.showMessageDialog(registerDialog, "GP registration successful!");
            } else if (role == UserRole.SPECIALIST) {
                // Clinician registration
                Clinician clinician = model.findClinicianById(id);
                if (clinician == null) {
                    JOptionPane.showMessageDialog(registerDialog, "Clinician ID not found. Please check with Admin.");
                    return;
                }
                if (model.isClinicianRegistered(id)) {
                    JOptionPane.showMessageDialog(registerDialog, "Clinician already registered. Please login instead.");
                    return;
                }
                model.registerClinicianWithPassword(id, password);
                JOptionPane.showMessageDialog(registerDialog, "Clinician registration successful!");
            } else {
                // Staff registration (for NURSE, RECEPTIONIST)
                Staff staff = model.findStaffById(id);
                if (staff == null) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff ID not found. Please check with Admin.");
                    return;
                }
                if (model.isStaffRegistered(id)) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff already registered. Please login instead.");
                    return;
                }
                model.registerStaffWithPassword(id, password, role);
                JOptionPane.showMessageDialog(registerDialog, "Staff registration successful!");
            }
            registerDialog.dispose();
        });
        panel.add(btnRegSubmit);

        JButton btnRegCancel = new JButton("Cancel");
        btnRegCancel.addActionListener(e -> registerDialog.dispose());
        panel.add(btnRegCancel);

        registerDialog.add(panel, BorderLayout.CENTER);
        registerDialog.setVisible(true);
    }
}