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

        // For PATIENT role, use NHS number; for GP use Staff ID; for SPECIALIST use Clinician ID; for RECEPTIONIST use Staff ID; for others use username
        if (role == UserRole.PATIENT) {
            contentPanel.add(new JLabel("NHS Number:"));
        } else if (role == UserRole.GP) {
            contentPanel.add(new JLabel("Staff ID:"));
        } else if (role == UserRole.SPECIALIST) {
            contentPanel.add(new JLabel("Clinician ID:"));
        } else if (role == UserRole.RECEPTIONIST) {
            contentPanel.add(new JLabel("Staff ID:"));
        } else {
            contentPanel.add(new JLabel("Username:"));
        }
        JTextField txtUsername = new JTextField();
        contentPanel.add(txtUsername);

        contentPanel.add(new JLabel("Password:"));
        JPasswordField txtPassword = new JPasswordField();
        contentPanel.add(txtPassword);

        add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            
            if (role == UserRole.PATIENT) {
                // Patient NHS-based login
                Patient patient = model.authenticatePatientByNHS(username, password);
                if (patient != null) {
                    User patientUser = new User(patient.getId(), patient.getId(), model.hashPassword(password), UserRole.PATIENT);
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), patient.getId(), "LOGIN", LocalDateTime.now(), "Patient logged in with NHS: " + username);
                    model.addAuditLog(loginLog);
                    mainView.setCurrentUser(patientUser);
                    dispose();
                    mainView.switchToDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid NHS number or password");
                }
            } else if (role == UserRole.GP) {
                // GP login using clinician_id and password (GPs are in clinicians.csv)
                Clinician clinician = model.findClinicianById(username);
                if (clinician != null) {
                    // Check if clinician has registered in users.csv
                    User gpUser = model.authenticateClinician(username, password);
                    if (gpUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome Dr. " + clinician.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), clinician.getId(), "LOGIN", LocalDateTime.now(), "GP logged in: Dr. " + clinician.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(gpUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid clinician ID or password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Clinician ID not found");
                }
            } else if (role == UserRole.SPECIALIST) {
                // Clinician login using clinician_id and password
                Clinician clinician = model.findClinicianById(username);
                if (clinician != null) {
                    // Check if clinician has registered in users.csv
                    User clinUser = model.authenticateClinician(username, password);
                    if (clinUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome Dr. " + clinician.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), clinician.getId(), "LOGIN", LocalDateTime.now(), "Clinician logged in: Dr. " + clinician.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(clinUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid clinician ID or password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Clinician ID not found");
                }
            } else if (role == UserRole.RECEPTIONIST) {
                // Receptionist login using staff_id and password
                Staff staff = model.findStaffById(username);
                if (staff != null) {
                    // Check if receptionist has registered in users.csv
                    User recepUser = model.authenticateStaff(username, password);
                    if (recepUser != null) {
                        JOptionPane.showMessageDialog(this, "Login successful! Welcome " + staff.getFirstName());
                        AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), staff.getId(), "LOGIN", LocalDateTime.now(), "Receptionist logged in: " + staff.getFirstName());
                        model.addAuditLog(loginLog);
                        mainView.setCurrentUser(recepUser);
                        dispose();
                        mainView.switchToDashboard();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid staff ID or password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Staff ID not found");
                }
            } else {
                // Standard authentication for other roles
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
        if (role.toString().equals("GP")) {
            labelText = "Clinician";
        }
        if (role.toString().equals("RECEPTIONIST")) {
            labelText = "Staff";
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
                // GP registration using Clinician ID (GPs are clinicians)
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
            } else if (role == UserRole.RECEPTIONIST) {
                // Receptionist registration using Staff ID
                Staff staff = model.findStaffById(id);
                if (staff == null) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff ID not found. Please check with Admin.");
                    return;
                }
                if (model.isStaffRegistered(id)) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff already registered. Please login instead.");
                    return;
                }
                model.registerReceptionistWithPassword(id, password);
                JOptionPane.showMessageDialog(registerDialog, "Receptionist registration successful!");
            } else {
                // Staff registration
                Staff staff = model.findStaffById(id);
                if (staff == null) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff ID not found. Please check with Admin.");
                    return;
                }
                if (model.isStaffRegistered(id)) {
                    JOptionPane.showMessageDialog(registerDialog, "Staff already registered. Please login instead.");
                    return;
                }
                model.registerStaffWithPassword(id, password);
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