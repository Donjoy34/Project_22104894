package view;

import view.UserRole;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainView extends JFrame {
    private Runnable onLoginSuccess;
    private JTable patientTable;
    private JTable referralTable;
    private JTable prescriptionTable;
    private JButton btnAddPatient;
    private JButton btnAddReferral;
    private JButton btnAddPrescription;

    public MainView() {
        setTitle("Healthcare Management System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Select User Role", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTitle, BorderLayout.NORTH);

        JPanel panelButtons = new JPanel(new GridLayout(1, 5, 10, 10));
        panelButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnPatient = new JButton("Patient");
        JButton btnGP = new JButton("GP");
        JButton btnNurse = new JButton("Nurse");
        JButton btnReceptionist = new JButton("Receptionist");
        JButton btnAdmin = new JButton("Admin");

        panelButtons.add(btnPatient);
        panelButtons.add(btnGP);
        panelButtons.add(btnNurse);
        panelButtons.add(btnReceptionist);
        panelButtons.add(btnAdmin);

        add(panelButtons, BorderLayout.CENTER);

        // Button actions
        btnPatient.addActionListener(e -> openLogin(UserRole.PATIENT));
        btnGP.addActionListener(e -> openLogin(UserRole.GP));
        btnNurse.addActionListener(e -> openLogin(UserRole.NURSE));
        btnReceptionist.addActionListener(e -> openLogin(UserRole.RECEPTIONIST));
        btnAdmin.addActionListener(e -> openLogin(UserRole.ADMIN));
    }

    public void setOnLoginSuccess(Runnable runnable) {
        this.onLoginSuccess = runnable;
    }

    private void openLogin(UserRole role) {
        LoginView loginView = new LoginView(role, this);
        loginView.setVisible(true);
    }

    public void switchToDashboard() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout());
        btnAddPatient = new JButton("Add Patient");
        btnAddReferral = new JButton("Add Referral");
        btnAddPrescription = new JButton("Add Prescription");
        topPanel.add(btnAddPatient);
        topPanel.add(btnAddReferral);
        topPanel.add(btnAddPrescription);
        add(topPanel, BorderLayout.NORTH);

        // Center panel with tables
        JTabbedPane tabbedPane = new JTabbedPane();
        patientTable = new JTable(new javax.swing.table.DefaultTableModel(new Object[]{"ID", "Name", "DOB", "NHS", "Details"}, 0));
        referralTable = new JTable(new javax.swing.table.DefaultTableModel(new Object[]{"ID", "Patient ID", "Urgency", "Summary", "Status"}, 0));
        prescriptionTable = new JTable(new javax.swing.table.DefaultTableModel(new Object[]{"ID", "Patient ID", "Medication", "Dosage"}, 0));

        tabbedPane.addTab("Patients", new JScrollPane(patientTable));
        tabbedPane.addTab("Referrals", new JScrollPane(referralTable));
        tabbedPane.addTab("Prescriptions", new JScrollPane(prescriptionTable));
        add(tabbedPane, BorderLayout.CENTER);

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
}
