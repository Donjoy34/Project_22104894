package view;

import model.User;
import model.DataManager;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

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
            btnManageUsers.addActionListener(e -> JOptionPane.showMessageDialog(this, "Manage Users functionality"));
            buttonPanel.add(btnManageUsers);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "ASSIGN_ROLES")) {
            JButton btnAssignRoles = new JButton("Assign Roles");
            btnAssignRoles.addActionListener(e -> JOptionPane.showMessageDialog(this, "Assign Roles functionality"));
            buttonPanel.add(btnAssignRoles);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "VIEW_LOGS")) {
            JButton btnViewLogs = new JButton("View System Logs");
            btnViewLogs.addActionListener(e -> JOptionPane.showMessageDialog(this, "System Logs:\n- User Login\n- Data Access\n- Changes"));
            buttonPanel.add(btnViewLogs);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "SYSTEM_SETTINGS")) {
            JButton btnSettings = new JButton("System Settings");
            btnSettings.addActionListener(e -> JOptionPane.showMessageDialog(this, "System Settings functionality"));
            buttonPanel.add(btnSettings);
        }

        if (AccessControl.hasPermission(UserRole.ADMIN, "DATA_BACKUP")) {
            JButton btnBackup = new JButton("Data Backup");
            btnBackup.addActionListener(e -> JOptionPane.showMessageDialog(this, "Data Backup functionality"));
            buttonPanel.add(btnBackup);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }
}
