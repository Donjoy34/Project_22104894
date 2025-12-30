package view;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private JTable patientTable;
    private JTable referralTable;
    private JTable prescriptionTable;
    private JButton btnAddPatient;
    private JButton btnAddReferral;
    private JButton btnAddPrescription;

    public DashboardView() {
        setTitle("Healthcare Management System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
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
    }

    public void setAddPatientListener(java.awt.event.ActionListener listener) {
        btnAddPatient.addActionListener(listener);
    }

    public void setAddReferralListener(java.awt.event.ActionListener listener) {
        btnAddReferral.addActionListener(listener);
    }

    public void setAddPrescriptionListener(java.awt.event.ActionListener listener) {
        btnAddPrescription.addActionListener(listener);
    }

    public void addPatientRow(Object[] row) {
        ((javax.swing.table.DefaultTableModel) patientTable.getModel()).addRow(row);
    }

    public void addReferralRow(Object[] row) {
        ((javax.swing.table.DefaultTableModel) referralTable.getModel()).addRow(row);
    }

    public void addPrescriptionRow(Object[] row) {
        ((javax.swing.table.DefaultTableModel) prescriptionTable.getModel()).addRow(row);
    }
}