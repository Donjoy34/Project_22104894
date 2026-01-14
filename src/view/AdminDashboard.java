package view;

import model.User;
import model.DataManager;
import model.AuditLog;
import model.Patient;
import model.Staff;
import model.Clinician;
import util.AccessControl;
import view.UserRole;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.nio.file.Path;

public class AdminDashboard extends JPanel {
    private final User admin;
    private final DataManager model;

    public AdminDashboard(User admin, DataManager model) {
        this.admin = admin;
        this.model = model;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Administrator Dashboard - " + admin.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.ADMIN, "MANAGE_USERS")) {
            JButton btnManageUsers = new JButton("Manage Users");
            btnManageUsers.addActionListener(e -> openManageUsersDialog());
            buttonPanel.add(btnManageUsers);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "MANAGE_USERS")) {
            JButton btnAddStaff = new JButton("Add Staff");
            btnAddStaff.addActionListener(e -> openAddStaffDialog());
            buttonPanel.add(btnAddStaff);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "MANAGE_USERS")) {
            JButton btnAddClinician = new JButton("Add Clinician");
            btnAddClinician.addActionListener(e -> openAddClinicianDialog());
            buttonPanel.add(btnAddClinician);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "ASSIGN_ROLES")) {
            JButton btnAssignRoles = new JButton("Assign Roles");
            btnAssignRoles.addActionListener(e -> openAssignRoleDialog());
            buttonPanel.add(btnAssignRoles);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "VIEW_LOGS")) {
            JButton btnViewLogs = new JButton("View System Logs");
            btnViewLogs.addActionListener(e -> openLogsDialog());
            buttonPanel.add(btnViewLogs);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "SYSTEM_SETTINGS")) {
            JButton btnSettings = new JButton("System Settings");
            btnSettings.addActionListener(e -> openSettingsDialog());
            buttonPanel.add(btnSettings);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "DATA_BACKUP")) {
            JButton btnBackup = new JButton("Data Backup");
            btnBackup.addActionListener(e -> performBackup());
            buttonPanel.add(btnBackup);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void openManageUsersDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Manage Users", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        String[] columns = {"ID", "Username", "First Name", "Last Name", "Role"};
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columns, 0);
        populateUserTable(tableModel);
        JTable table = new JTable(tableModel);

        JButton btnAdd = new JButton("Add User");
        btnAdd.addActionListener(e -> {
            JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
            JTextField txtUsername = new JTextField();
            JPasswordField txtPassword = new JPasswordField();
            JComboBox<UserRole> roleBox = new JComboBox<>(UserRole.values());
            form.add(new JLabel("Username:"));
            form.add(txtUsername);
            form.add(new JLabel("Password:"));
            form.add(txtPassword);
            form.add(new JLabel("Role:"));
            form.add(roleBox);

            int res = JOptionPane.showConfirmDialog(dialog, form, "Add User", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword()).trim();
                UserRole role = (UserRole) roleBox.getSelectedItem();
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username and password are required.");
                    return;
                }
                if (model.usernameExists(username)) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists.");
                    return;
                }
                String id = model.generateUserId("U");
                User newUser = new User(id, username, password, role);
                model.addUser(newUser);
                tableModel.addRow(new Object[]{id, username, "", "", role});
            }
        });

        JButton btnImport = new JButton("Import from CSVs");
        btnImport.addActionListener(e -> {
            int imported = importAllFromData(tableModel);
            JOptionPane.showMessageDialog(dialog, "Imported " + imported + " users from staff/clinicians/patients.");
        });

        JButton btnDelete = new JButton("Delete User");
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(dialog, "Select a user row to delete.");
                return;
            }
            String username = tableModel.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete user '" + username + "' ?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
            if (confirm == JOptionPane.OK_OPTION) {
                boolean removed = model.deleteUserByUsername(username);
                if (removed) {
                    tableModel.removeRow(row);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Delete failed: user not found.");
                }
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnAdd);
        bottom.add(btnImport);
        bottom.add(btnDelete);

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void populateUserTable(javax.swing.table.DefaultTableModel tm) {
        tm.setRowCount(0);
        for (User u : model.getUsers()) {
            String fn = "";
            String ln = "";
            switch (u.getRole()) {
                case PATIENT:
                    Patient p = model.findPatientById(u.getUsername());
                    if (p != null) { fn = p.getFirstName(); ln = p.getLastName(); }
                    break;
                case GP:
                case NURSE:
                case RECEPTIONIST:
                    Staff s = model.findStaffById(u.getUsername());
                    if (s != null) { fn = s.getFirstName(); ln = s.getLastName(); }
                    break;
                case SPECIALIST:
                    Clinician c = model.findClinicianById(u.getUsername());
                    if (c != null) { fn = c.getFirstName(); ln = c.getLastName(); }
                    break;
                default:
                    break;
            }
            tm.addRow(new Object[]{u.getId(), u.getUsername(), fn, ln, u.getRole()});
        }
    }

    private int importAllFromData(javax.swing.table.DefaultTableModel tm) {
        int imported = 0;
        // Patients
        for (Patient p : model.getPatients()) {
            if (!model.usernameExists(p.getId())) {
                String id = model.generateUserId("UP");
                User u = new User(id, p.getId(), "password", UserRole.PATIENT);
                model.addUser(u);
                tm.addRow(new Object[]{id, p.getId(), p.getFirstName(), p.getLastName(), UserRole.PATIENT});
                imported++;
            }
        }
        // Clinicians
        for (Clinician c : model.getClinicians()) {
            if (!model.usernameExists(c.getId())) {
                String id = model.generateUserId("UC");
                User u = new User(id, c.getId(), "password", UserRole.SPECIALIST);
                model.addUser(u);
                tm.addRow(new Object[]{id, c.getId(), c.getFirstName(), c.getLastName(), UserRole.SPECIALIST});
                imported++;
            }
        }
        // Staff (map role heuristically)
        for (Staff s : model.getStaffs()) {
            if (!model.usernameExists(s.getId())) {
                UserRole role = mapStaffRole(s.getRole());
                String id = model.generateUserId("US");
                User u = new User(id, s.getId(), "password", role);
                model.addUser(u);
                tm.addRow(new Object[]{id, s.getId(), s.getFirstName(), s.getLastName(), role});
                imported++;
            }
        }
        return imported;
    }

    private UserRole mapStaffRole(String roleText) {
        if (roleText == null) return UserRole.GP;
        String r = roleText.toLowerCase();
        if (r.contains("reception")) return UserRole.RECEPTIONIST;
        if (r.contains("nurse")) return UserRole.NURSE;
        if (r.contains("gp")) return UserRole.GP;
        return UserRole.GP;
    }

    private void openAssignRoleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Assign Role", true);
        dialog.setSize(450, 200);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField txtUsername = new JTextField();
        JComboBox<UserRole> roleBox = new JComboBox<>(UserRole.values());
        form.add(new JLabel("Username:"));
        form.add(txtUsername);
        form.add(new JLabel("New Role:"));
        form.add(roleBox);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            String username = txtUsername.getText().trim();
            UserRole newRole = (UserRole) roleBox.getSelectedItem();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username is required.");
                return;
            }
            boolean updated = model.updateUserRole(username, newRole);
            if (updated) {
                JOptionPane.showMessageDialog(dialog, "Role updated.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "User not found.");
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openLogsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Audit Logs", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"ID", "User ID", "Action", "Timestamp", "Details"};
        javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(cols, 0);
        for (AuditLog log : model.getAuditLogs()) {
            tm.addRow(new Object[]{log.getId(), log.getUserId(), log.getAction(), log.getTimestamp(), log.getDetails()});
        }
        JTable table = new JTable(tm);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton close = new JButton("Close");
        close.addActionListener(e -> dialog.dispose());
        dialog.add(close, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openSettingsDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        JCheckBox chkAutoBackup = new JCheckBox("Enable Auto Backup (mock)");
        JCheckBox chkAuditAlerts = new JCheckBox("Enable Audit Alerts (mock)");
        JCheckBox chkForceStrongPw = new JCheckBox("Require Strong Passwords (mock)");
        panel.add(chkAutoBackup);
        panel.add(chkAuditAlerts);
        panel.add(chkForceStrongPw);
        JOptionPane.showMessageDialog(this, panel, "System Settings", JOptionPane.PLAIN_MESSAGE);
    }

    private void performBackup() {
        Path backupDir = model.backupDatabase();
        if (backupDir != null) {
            JOptionPane.showMessageDialog(this, "Backup completed: " + backupDir.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Backup failed. See logs for details.");
        }
    }

    private void openAddStaffDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Staff", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Staff ID (auto-generated)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Staff ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtStaffId = new JTextField(generateNextStaffId());
        txtStaffId.setEditable(false);
        form.add(txtStaffId, gbc);
        row++;

        // First Name
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtFirstName = new JTextField();
        form.add(txtFirstName, gbc);
        row++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtLastName = new JTextField();
        form.add(txtLastName, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JTextField txtRole = new JTextField();
        form.add(txtRole, gbc);
        row++;

        // Department (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        String[] departments = {"Administration", "Front Desk", "Clinical Support", "Support Services"};
        JComboBox<String> cbDepartment = new JComboBox<>(departments);
        form.add(cbDepartment, gbc);
        row++;

        // Facility ID (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Facility ID:"), gbc);
        gbc.gridx = 1;
        String[] facilities = {"S001", "S002", "S003", "H001", "H002", "H003"};
        JComboBox<String> cbFacility = new JComboBox<>(facilities);
        form.add(cbFacility, gbc);
        row++;

        // Phone Number
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField();
        form.add(txtPhone, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField();
        form.add(txtEmail, gbc);
        row++;

        // Employment Status (radio buttons)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Employment Status:"), gbc);
        gbc.gridx = 1;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup statusGroup = new ButtonGroup();
        JRadioButton rbFullTime = new JRadioButton("Full-time", true);
        JRadioButton rbPartTime = new JRadioButton("Part-time");
        statusGroup.add(rbFullTime);
        statusGroup.add(rbPartTime);
        statusPanel.add(rbFullTime);
        statusPanel.add(rbPartTime);
        form.add(statusPanel, gbc);
        row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField txtStartDate = new JTextField();
        form.add(txtStartDate, gbc);
        row++;

        // Line Manager (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Line Manager:"), gbc);
        gbc.gridx = 1;
        String[] managers = {"Dr. David Thompson", "Michelle Adams", "Dr. Mark Davies", "Sandra Brown", 
                             "Dr. Susan Clarke", "Margaret Taylor", "Sarah Mitchell", "David Thompson", 
                             "Michael Brown", "Susan Clarke", "Emma Thompson"};
        JComboBox<String> cbManager = new JComboBox<>(managers);
        form.add(cbManager, gbc);
        row++;

        // Access Level (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Access Level:"), gbc);
        gbc.gridx = 1;
        String[] accessLevels = {"Basic", "Standard", "Manager"};
        JComboBox<String> cbAccessLevel = new JComboBox<>(accessLevels);
        form.add(cbAccessLevel, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            String staffId = txtStaffId.getText().trim();
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String role = txtRole.getText().trim();
            String department = (String) cbDepartment.getSelectedItem();
            String facilityId = (String) cbFacility.getSelectedItem();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String employmentStatus = rbFullTime.isSelected() ? "Full-time" : "Part-time";
            String startDate = txtStartDate.getText().trim();
            String lineManager = (String) cbManager.getSelectedItem();
            String accessLevel = (String) cbAccessLevel.getSelectedItem();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name, last name, and role are required.");
                return;
            }

            // Create Staff object
            Staff newStaff = new Staff(staffId, firstName, lastName, role, department, facilityId,
                                       phone, email, employmentStatus, startDate, lineManager, accessLevel);
            model.addStaff(newStaff);
            
            JOptionPane.showMessageDialog(dialog, "Staff added successfully: " + staffId);
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String generateNextStaffId() {
        int maxId = 0;
        for (Staff s : model.getStaffs()) {
            String id = s.getId();
            if (id.startsWith("ST")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // skip invalid IDs
                }
            }
        }
        return String.format("ST%03d", maxId + 1);
    }

    private void openAddClinicianDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Clinician", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Clinician ID (auto-generated)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Clinician ID:"), gbc);
        gbc.gridx = 1;
        JTextField txtClinicianId = new JTextField(generateNextClinicianId());
        txtClinicianId.setEditable(false);
        form.add(txtClinicianId, gbc);
        row++;

        // First Name
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtFirstName = new JTextField();
        form.add(txtFirstName, gbc);
        row++;

        // Last Name
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        JTextField txtLastName = new JTextField();
        form.add(txtLastName, gbc);
        row++;

        // Title (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        String[] titles = {"Dr.", "GP", "Consultant", "Sister", "Nurse"};
        JComboBox<String> cbTitle = new JComboBox<>(titles);
        form.add(cbTitle, gbc);
        row++;

        // Speciality (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Speciality:"), gbc);
        gbc.gridx = 1;
        String[] specialities = {"General Practice", "Cardiology", "Neurology", "Orthopaedics", 
                                 "Dermatology", "General Nursing", "Practice Nursing", 
                                 "Gastroenterology", "Emergency Medicine", "Oncology"};
        JComboBox<String> cbSpeciality = new JComboBox<>(specialities);
        form.add(cbSpeciality, gbc);
        row++;

        // GMC Number
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("GMC Number:"), gbc);
        gbc.gridx = 1;
        JTextField txtGmcNumber = new JTextField();
        form.add(txtGmcNumber, gbc);
        row++;

        // Phone Number
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        JTextField txtPhone = new JTextField();
        form.add(txtPhone, gbc);
        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField txtEmail = new JTextField();
        form.add(txtEmail, gbc);
        row++;

        // Workplace ID (dropdown)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Workplace ID:"), gbc);
        gbc.gridx = 1;
        String[] workplaces = {"S001", "S002", "S003", "H001", "H002", "H003"};
        JComboBox<String> cbWorkplace = new JComboBox<>(workplaces);
        form.add(cbWorkplace, gbc);
        row++;

        // Workplace Type
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Workplace Type:"), gbc);
        gbc.gridx = 1;
        JTextField txtWorkplaceType = new JTextField("GP Surgery");
        form.add(txtWorkplaceType, gbc);
        row++;

        // Employment Status (radio buttons)
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Employment Status:"), gbc);
        gbc.gridx = 1;
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup statusGroup = new ButtonGroup();
        JRadioButton rbFullTime = new JRadioButton("Full-time", true);
        JRadioButton rbPartTime = new JRadioButton("Part-time");
        statusGroup.add(rbFullTime);
        statusGroup.add(rbPartTime);
        statusPanel.add(rbFullTime);
        statusPanel.add(rbPartTime);
        form.add(statusPanel, gbc);
        row++;

        // Start Date
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        JTextField txtStartDate = new JTextField();
        form.add(txtStartDate, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            String clinicianId = txtClinicianId.getText().trim();
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String title = (String) cbTitle.getSelectedItem();
            String speciality = (String) cbSpeciality.getSelectedItem();
            String gmcNumber = txtGmcNumber.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String workplaceId = (String) cbWorkplace.getSelectedItem();
            String workplaceType = txtWorkplaceType.getText().trim();
            String employmentStatus = rbFullTime.isSelected() ? "Full-time" : "Part-time";
            String startDate = txtStartDate.getText().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty() || gmcNumber.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name, last name, and GMC number are required.");
                return;
            }

            // Create Clinician object
            Clinician newClinician = new Clinician(clinicianId, firstName, lastName, title, speciality,
                                                   gmcNumber, phone, email, workplaceId, workplaceType,
                                                   employmentStatus, startDate);
            model.addClinician(newClinician);
            
            JOptionPane.showMessageDialog(dialog, "Clinician added successfully: " + clinicianId);
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(new JScrollPane(form), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private String generateNextClinicianId() {
        int maxId = 0;
        for (Clinician c : model.getClinicians()) {
            String id = c.getId();
            if (id.startsWith("C")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > maxId) maxId = num;
                } catch (NumberFormatException e) {
                    // skip invalid IDs
                }
            }
        }
        return String.format("C%03d", maxId + 1);
    }
}
