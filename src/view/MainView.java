package view;

import view.UserRole;
import model.DataManager;
import model.User;
import model.AuditLog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class MainView extends JFrame {
    private Runnable onLoginSuccess;
    private JTable patientTable;
    private JTable referralTable;
    private JTable prescriptionTable;
    private JButton btnAddPatient;
    private JButton btnAddReferral;
    private JButton btnAddPrescription;
    private JButton btnLogout;
    private JButton btnViewHistory;
    private DataManager model;
    private User currentUser;

    public MainView(DataManager model) {
        this.model = model;
        setTitle("Healthcare Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Select User Role", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(2, 3, 10, 10));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnPatient = new JButton("Patient");
        JButton btnGP = new JButton("GP");
        JButton btnSpecialist = new JButton("Specialist");
        JButton btnNurse = new JButton("Nurse");
        JButton btnReceptionist = new JButton("Receptionist");
        JButton btnAdmin = new JButton("Admin");

        panelButtons.add(btnPatient);
        panelButtons.add(btnGP);
        panelButtons.add(btnSpecialist);
        panelButtons.add(btnNurse);
        panelButtons.add(btnReceptionist);
        panelButtons.add(btnAdmin);

        add(panelButtons, BorderLayout.CENTER);

        // Button actions
        btnPatient.addActionListener(e -> openLogin(UserRole.PATIENT));
        btnGP.addActionListener(e -> openLogin(UserRole.GP));
        btnSpecialist.addActionListener(e -> openLogin(UserRole.SPECIALIST));
        btnNurse.addActionListener(e -> openLogin(UserRole.NURSE));
        btnReceptionist.addActionListener(e -> openLogin(UserRole.RECEPTIONIST));
        btnAdmin.addActionListener(e -> openLogin(UserRole.ADMIN));
    }

    public void setOnLoginSuccess(Runnable runnable) {
        this.onLoginSuccess = runnable;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void openLogin(UserRole role) {
        LoginView loginView = new LoginView(role, this, model);
        loginView.setVisible(true);
    }

    public void switchToDashboard() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Create role-based dashboard
        JPanel dashboard = DashboardFactory.createDashboard(currentUser, model);
        add(dashboard, BorderLayout.CENTER);

        // Bottom panel with logout button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        if (onLoginSuccess != null) {
            onLoginSuccess.run();
        }
    }

    // Methods for controller
    public void setAddPatientListener(ActionListener listener) {
        if (btnAddPatient != null) btnAddPatient.addActionListener(listener);
    }

    public void setAddReferralListener(ActionListener listener) {
        if (btnAddReferral != null) btnAddReferral.addActionListener(listener);
    }

    public void setAddPrescriptionListener(ActionListener listener) {
        if (btnAddPrescription != null) btnAddPrescription.addActionListener(listener);
    }

    public void addPatientRow(Object[] row) {
        if (patientTable != null) ((javax.swing.table.DefaultTableModel) patientTable.getModel()).addRow(row);
    }

    public void addReferralRow(Object[] row) {
        if (referralTable != null) ((javax.swing.table.DefaultTableModel) referralTable.getModel()).addRow(row);
    }

    public void addPrescriptionRow(Object[] row) {
        if (prescriptionTable != null) ((javax.swing.table.DefaultTableModel) prescriptionTable.getModel()).addRow(row);
    }

    private void logout() {
        // Log logout
        if (currentUser != null) {
            AuditLog logoutLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), currentUser.getId(), "LOGOUT", LocalDateTime.now(), "User logged out");
            model.addAuditLog(logoutLog);
        }
        // Switch back to home screen
        switchToHome();
    }

    private void switchToHome() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Select User Role", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(2, 3, 10, 10));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnPatient = new JButton("Patient");
        JButton btnGP = new JButton("GP");
        JButton btnSpecialist = new JButton("Specialist");
        JButton btnNurse = new JButton("Nurse");
        JButton btnReceptionist = new JButton("Receptionist");
        JButton btnAdmin = new JButton("Admin");

        btnPatient.setPreferredSize(new Dimension(120, 60));
        btnGP.setPreferredSize(new Dimension(120, 60));
        btnSpecialist.setPreferredSize(new Dimension(120, 60));
        btnNurse.setPreferredSize(new Dimension(120, 60));
        btnReceptionist.setPreferredSize(new Dimension(120, 60));
        btnAdmin.setPreferredSize(new Dimension(120, 60));

        panelButtons.add(btnPatient);
        panelButtons.add(btnGP);
        panelButtons.add(btnSpecialist);
        panelButtons.add(btnNurse);
        panelButtons.add(btnReceptionist);
        panelButtons.add(btnAdmin);

        add(panelButtons, BorderLayout.CENTER);

        // Button actions
        btnPatient.addActionListener(e -> openLogin(UserRole.PATIENT));
        btnGP.addActionListener(e -> openLogin(UserRole.GP));
        btnSpecialist.addActionListener(e -> openLogin(UserRole.SPECIALIST));
        btnNurse.addActionListener(e -> openLogin(UserRole.NURSE));
        btnReceptionist.addActionListener(e -> openLogin(UserRole.RECEPTIONIST));
        btnAdmin.addActionListener(e -> openLogin(UserRole.ADMIN));

        revalidate();
        repaint();
    }

    private void openHistoryDialog() {
        JDialog historyDialog = new JDialog(this, "Audit History", true);
        historyDialog.setSize(800, 600);
        historyDialog.setLayout(new BorderLayout());

        JTable historyTable = new JTable(new javax.swing.table.DefaultTableModel(new Object[]{"ID", "User ID", "Action", "Timestamp", "Details"}, 0));
        for (AuditLog log : model.getAuditLogs()) {
            ((javax.swing.table.DefaultTableModel) historyTable.getModel()).addRow(new Object[]{
                log.getId(), log.getUserId(), log.getAction(), log.getTimestamp().toString(), log.getDetails()
            });
        }

        historyDialog.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> historyDialog.dispose());
        historyDialog.add(btnClose, BorderLayout.SOUTH);

        historyDialog.setVisible(true);
    }
}
