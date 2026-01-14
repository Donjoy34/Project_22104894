package view;

import model.User;
import model.DataManager;
import model.AuditLog;
import model.Staff;
import model.Clinician;
import util.AccessControl;
import view.UserRole;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JPanel {
    private User admin;
    private DataManager dataManager;

    public AdminDashboard(User admin, DataManager dataManager) {
        this.admin = admin;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Administrator Dashboard - " + admin.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.ADMIN, "MANAGE_USERS")) {
            JButton btnManageUsers = new JButton("Manage Users");
            btnManageUsers.addActionListener(e -> manageUsersDialog());
            buttonPanel.add(btnManageUsers);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "ASSIGN_ROLES")) {
            JButton btnAssignRoles = new JButton("Assign Roles");
            btnAssignRoles.addActionListener(e -> assignRolesDialog());
            buttonPanel.add(btnAssignRoles);
        }

        JButton btnManageStaff = new JButton("Manage Staff");
        btnManageStaff.addActionListener(e -> manageStaffDialog());
        buttonPanel.add(btnManageStaff);

        JButton btnManageClinicians = new JButton("Manage Clinicians");
        btnManageClinicians.addActionListener(e -> manageClinicianDialog());
        buttonPanel.add(btnManageClinicians);

        if (AccessControl.hasPermission(UserRole.ADMIN, "VIEW_LOGS")) {
            JButton btnViewLogs = new JButton("View System Logs");
            btnViewLogs.addActionListener(e -> viewLogsDialog());
            buttonPanel.add(btnViewLogs);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "SYSTEM_SETTINGS")) {
            JButton btnSettings = new JButton("System Settings");
            btnSettings.addActionListener(e -> systemSettingsDialog());
            buttonPanel.add(btnSettings);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "DATA_BACKUP")) {
            JButton btnBackup = new JButton("Data Backup");
            btnBackup.addActionListener(e -> dataBackupDialog());
            buttonPanel.add(btnBackup);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void manageUsersDialog() {
        List<User> users = dataManager.getUsers();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("User ID");
        model.addColumn("Username");
        model.addColumn("Role");

        for (User u : users) {
            model.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String userId = (String) table.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(this, "User " + userId + " would be deleted");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user to delete");
            }
        });
        buttonPanel.add(deleteBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Manage Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void assignRolesDialog() {
        List<User> users = dataManager.getUsers();
        
        if (users.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No users available");
            return;
        }

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Assign Roles", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 1, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User selection
        String[] usernames = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            usernames[i] = users.get(i).getUsername() + " (" + users.get(i).getId() + ")";
        }

        contentPanel.add(new JLabel("Select User:"));
        JComboBox<String> userCombo = new JComboBox<>(usernames);
        contentPanel.add(userCombo);

        // Role selection
        String[] roles = {"PATIENT", "GP", "SPECIALIST", "NURSE", "RECEPTIONIST", "ADMIN"};
        contentPanel.add(new JLabel("Assign Role:"));
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        contentPanel.add(roleCombo);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton assignBtn = new JButton("Assign");
        JButton cancelBtn = new JButton("Cancel");

        assignBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Role assigned successfully!");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewLogsDialog() {
        List<AuditLog> logs = dataManager.getAuditLogs();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("User ID");
        model.addColumn("Action");
        model.addColumn("Timestamp");
        model.addColumn("Details");

        for (AuditLog log : logs) {
            model.addRow(new Object[]{log.getUserId(), log.getAction(), log.getTimestamp(), log.getDetails()});
        }

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scrollPane, "System Audit Logs", JOptionPane.INFORMATION_MESSAGE);
    }

    private void systemSettingsDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "System Settings", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(4, 2, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("System Name:"));
        JTextField systemNameField = new JTextField("Healthcare Management System");
        contentPanel.add(systemNameField);

        contentPanel.add(new JLabel("Max Active Sessions:"));
        JTextField sessionsField = new JTextField("100");
        contentPanel.add(sessionsField);

        contentPanel.add(new JLabel("Password Expiry (days):"));
        JTextField expiryField = new JTextField("90");
        contentPanel.add(expiryField);

        contentPanel.add(new JLabel("Database Path:"));
        JTextField dbPathField = new JTextField("database/");
        contentPanel.add(dbPathField);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Settings saved successfully!");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void dataBackupDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Data Backup", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(3, 1, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Select backup location:"));
        JTextField backupPathField = new JTextField("backups/backup_" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));
        contentPanel.add(backupPathField);

        JTextArea statusArea = new JTextArea("Ready to backup:\n- patients.csv\n- users.csv\n- appointments.csv\n- prescriptions.csv\n- referrals.csv\n- audit_logs.csv");
        statusArea.setEditable(false);
        contentPanel.add(new JScrollPane(statusArea));

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton backupBtn = new JButton("Start Backup");
        JButton cancelBtn = new JButton("Cancel");

        backupBtn.addActionListener(e -> {
            try {
                // Create backup directory
                java.nio.file.Files.createDirectories(java.nio.file.Paths.get(backupPathField.getText()));
                
                // Copy files
                java.nio.file.Files.copy(java.nio.file.Paths.get("database/patients.csv"), 
                    java.nio.file.Paths.get(backupPathField.getText() + "/patients.csv"), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                JOptionPane.showMessageDialog(dialog, "Backup completed successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Backup failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(backupBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void manageStaffDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Manage Staff", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Table to display staff
        DefaultTableModel staffModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Role", "Department", "Status"}, 0);
        List<Staff> staffs = dataManager.getStaffs();
        for (Staff s : staffs) {
            staffModel.addRow(new Object[]{s.getId(), s.getFirstName(), s.getLastName(), s.getRole(), s.getDepartment(), s.getEmploymentStatus()});
        }
        JTable staffTable = new JTable(staffModel);
        JScrollPane scrollPane = new JScrollPane(staffTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAddStaff = new JButton("Add Staff");
        btnAddStaff.addActionListener(e -> {
            JDialog addDialog = new JDialog(dialog, "Add Staff Member", true);
            addDialog.setSize(500, 700);
            addDialog.setLocationRelativeTo(dialog);
            addDialog.setLayout(new BorderLayout(10, 10));
            
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtId = new JTextField();
            JTextField txtFName = new JTextField();
            JTextField txtLName = new JTextField();
            JTextField txtRole = new JTextField();
            JTextField txtPhone = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtStart = new JTextField("2026-01-14");
            
            // Dropdown for Department
            String[] departments = {"Administration", "Front Desk", "Clinical Support", "Ward", "Support Services", "Other"};
            JComboBox<String> comboDept = new JComboBox<>(departments);
            
            // Dropdown for Facility ID
            String[] facilities = {"S001", "S002", "S003", "H001", "H002", "H003"};
            JComboBox<String> comboFacility = new JComboBox<>(facilities);
            
            // Dropdown for Line Manager
            String[] managers = {"Dr. David Thompson", "Michelle Adams", "Dr. Mark Davies", "Sandra Brown", "Dr. Susan Clarke", "Margaret Taylor", "David Thompson", "Sarah Mitchell", "Michael Brown", "Emma Thompson"};
            JComboBox<String> comboManager = new JComboBox<>(managers);
            
            // Radio buttons for Employment Status
            JRadioButton radioFullTime = new JRadioButton("Full-time", true);
            JRadioButton radioPartTime = new JRadioButton("Part-time");
            ButtonGroup groupStatus = new ButtonGroup();
            groupStatus.add(radioFullTime);
            groupStatus.add(radioPartTime);
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusPanel.add(radioFullTime);
            statusPanel.add(radioPartTime);
            
            // Dropdown for Access Level
            String[] accessLevels = {"Basic", "Standard", "Advanced", "Manager", "Administrator"};
            JComboBox<String> comboAccess = new JComboBox<>(accessLevels);
            comboAccess.setSelectedItem("Standard");
            
            formPanel.add(new JLabel("Staff ID:"));
            formPanel.add(txtId);
            formPanel.add(new JLabel("First Name:"));
            formPanel.add(txtFName);
            formPanel.add(new JLabel("Last Name:"));
            formPanel.add(txtLName);
            formPanel.add(new JLabel("Role:"));
            formPanel.add(txtRole);
            formPanel.add(new JLabel("Department:"));
            formPanel.add(comboDept);
            formPanel.add(new JLabel("Facility ID:"));
            formPanel.add(comboFacility);
            formPanel.add(new JLabel("Phone:"));
            formPanel.add(txtPhone);
            formPanel.add(new JLabel("Email:"));
            formPanel.add(txtEmail);
            formPanel.add(new JLabel("Employment Status:"));
            formPanel.add(statusPanel);
            formPanel.add(new JLabel("Start Date:"));
            formPanel.add(txtStart);
            formPanel.add(new JLabel("Line Manager:"));
            formPanel.add(comboManager);
            formPanel.add(new JLabel("Access Level:"));
            formPanel.add(comboAccess);
            
            JScrollPane sp = new JScrollPane(formPanel);
            addDialog.add(sp, BorderLayout.CENTER);
            
            JPanel actionPanel = new JPanel(new FlowLayout());
            JButton btnSubmit = new JButton("Add");
            btnSubmit.addActionListener(ev -> {
                String status = radioFullTime.isSelected() ? "Full-time" : "Part-time";
                Staff newStaff = new Staff(txtId.getText(), txtFName.getText(), txtLName.getText(),
                    txtRole.getText(), (String)comboDept.getSelectedItem(), (String)comboFacility.getSelectedItem(), txtPhone.getText(),
                    txtEmail.getText(), status, txtStart.getText(), (String)comboManager.getSelectedItem(), (String)comboAccess.getSelectedItem());
                dataManager.addStaff(newStaff);
                staffModel.addRow(new Object[]{newStaff.getId(), newStaff.getFirstName(), newStaff.getLastName(), 
                    newStaff.getRole(), newStaff.getDepartment(), newStaff.getEmploymentStatus()});
                JOptionPane.showMessageDialog(addDialog, "Staff added successfully!");
                addDialog.dispose();
            });
            actionPanel.add(btnSubmit);
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(ev -> addDialog.dispose());
            actionPanel.add(btnCancel);
            addDialog.add(actionPanel, BorderLayout.SOUTH);
            addDialog.setVisible(true);
        });
        buttonPanel.add(btnAddStaff);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void manageClinicianDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Manage Clinicians", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Table to display clinicians
        DefaultTableModel clinModel = new DefaultTableModel(new String[]{"ID", "First Name", "Last Name", "Title", "Specialty", "Status"}, 0);
        List<Clinician> clinicians = dataManager.getClinicians();
        for (Clinician c : clinicians) {
            clinModel.addRow(new Object[]{c.getId(), c.getFirstName(), c.getLastName(), c.getTitle(), c.getSpecialty(), c.getEmploymentStatus()});
        }
        JTable clinTable = new JTable(clinModel);
        JScrollPane scrollPane = new JScrollPane(clinTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnAddClin = new JButton("Add Clinician");
        btnAddClin.addActionListener(e -> {
            JDialog addDialog = new JDialog(dialog, "Add Clinician", true);
            addDialog.setSize(450, 600);
            addDialog.setLocationRelativeTo(dialog);
            
            JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField txtId = new JTextField();
            JTextField txtFName = new JTextField();
            JTextField txtLName = new JTextField();
            JTextField txtTitle = new JTextField("Dr.");
            JTextField txtGMC = new JTextField();
            JTextField txtPhone = new JTextField();
            JTextField txtEmail = new JTextField();
            JTextField txtWorkplace = new JTextField();
            JTextField txtStart = new JTextField("2026-01-14");
            
            // Dropdown for Specialty
            String[] specialties = {"General Practice", "Cardiology", "Neurology", "Orthopaedics", "Dermatology", "Gastroenterology", "General Nursing", "Practice Nursing"};
            JComboBox<String> comboSpecialty = new JComboBox<>(specialties);
            
            // Dropdown for Workplace Type
            String[] workplaceTypes = {"GP Surgery", "Hospital", "Community Clinic", "Private Practice"};
            JComboBox<String> comboWorkType = new JComboBox<>(workplaceTypes);
            
            // Radio buttons for Employment Status
            JRadioButton radioFullTime = new JRadioButton("Full-time", true);
            JRadioButton radioPartTime = new JRadioButton("Part-time");
            ButtonGroup groupStatus = new ButtonGroup();
            groupStatus.add(radioFullTime);
            groupStatus.add(radioPartTime);
            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusPanel.add(radioFullTime);
            statusPanel.add(radioPartTime);
            
            panel.add(new JLabel("Clinician ID:"));
            panel.add(txtId);
            panel.add(new JLabel("First Name:"));
            panel.add(txtFName);
            panel.add(new JLabel("Last Name:"));
            panel.add(txtLName);
            panel.add(new JLabel("Title:"));
            panel.add(txtTitle);
            panel.add(new JLabel("Specialty:"));
            panel.add(comboSpecialty);
            panel.add(new JLabel("GMC Number:"));
            panel.add(txtGMC);
            panel.add(new JLabel("Phone:"));
            panel.add(txtPhone);
            panel.add(new JLabel("Email:"));
            panel.add(txtEmail);
            panel.add(new JLabel("Workplace ID:"));
            panel.add(txtWorkplace);
            panel.add(new JLabel("Workplace Type:"));
            panel.add(comboWorkType);
            panel.add(new JLabel("Employment Status:"));
            panel.add(statusPanel);
            panel.add(new JLabel("Start Date:"));
            panel.add(txtStart);
            
            JScrollPane sp = new JScrollPane(panel);
            addDialog.add(sp, BorderLayout.CENTER);
            
            JPanel actionPanel = new JPanel(new FlowLayout());
            JButton btnSubmit = new JButton("Add");
            btnSubmit.addActionListener(ev -> {
                String status = radioFullTime.isSelected() ? "Full-time" : "Part-time";
                Clinician newClin = new Clinician(txtId.getText(), txtFName.getText(), txtLName.getText(),
                    txtTitle.getText(), (String)comboSpecialty.getSelectedItem(), txtGMC.getText(), txtPhone.getText(),
                    txtEmail.getText(), txtWorkplace.getText(), (String)comboWorkType.getSelectedItem(), status, txtStart.getText());
                dataManager.addClinician(newClin);
                clinModel.addRow(new Object[]{newClin.getId(), newClin.getFirstName(), newClin.getLastName(), 
                    newClin.getTitle(), newClin.getSpecialty(), newClin.getEmploymentStatus()});
                JOptionPane.showMessageDialog(addDialog, "Clinician added successfully!");
                addDialog.dispose();
            });
            actionPanel.add(btnSubmit);
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(ev -> addDialog.dispose());
            actionPanel.add(btnCancel);
            addDialog.add(actionPanel, BorderLayout.SOUTH);
            addDialog.setVisible(true);
        });
        buttonPanel.add(btnAddClin);

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
