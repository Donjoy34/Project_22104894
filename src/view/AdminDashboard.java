package view;

import model.User;
import model.DataManager;
import model.AuditLog;
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
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
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
}
