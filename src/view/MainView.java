package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainView extends JFrame {
    private JTable patientTable, referralTable, prescriptionTable;
    private DefaultTableModel patientModel, referralModel, prescriptionModel;
    private JButton btnAddPatient, btnAddReferral, btnAddPrescription;

    public MainView() {
        setTitle("Healthcare Management System (Part 2)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // 1. Patient Tab
        JPanel patientPanel = new JPanel(new BorderLayout());
        patientModel = new DefaultTableModel(new String[]{"ID", "Name", "DOB", "NHS No", "Phone"}, 0);
        patientTable = new JTable(patientModel);
        btnAddPatient = new JButton("Add New Patient");
        patientPanel.add(new JScrollPane(patientTable), BorderLayout.CENTER);
        patientPanel.add(btnAddPatient, BorderLayout.SOUTH);
        tabs.addTab("Patients", patientPanel);

        // 2. Referral Tab
        JPanel refPanel = new JPanel(new BorderLayout());
        referralModel = new DefaultTableModel(new String[]{"ID", "Patient ID", "Urgency", "Summary", "Status"}, 0);
        referralTable = new JTable(referralModel);
        btnAddReferral = new JButton("Create New Referral");
        refPanel.add(new JScrollPane(referralTable), BorderLayout.CENTER);
        refPanel.add(btnAddReferral, BorderLayout.SOUTH);
        tabs.addTab("Referrals", refPanel);

        // 3. Prescription Tab
        JPanel prescPanel = new JPanel(new BorderLayout());
        prescriptionModel = new DefaultTableModel(new String[]{"ID", "Patient ID", "Medication", "Dosage"}, 0);
        prescriptionTable = new JTable(prescriptionModel);
        btnAddPrescription = new JButton("Issue Prescription");
        prescPanel.add(new JScrollPane(prescriptionTable), BorderLayout.CENTER);
        prescPanel.add(btnAddPrescription, BorderLayout.SOUTH);
        tabs.addTab("Prescriptions", prescPanel);

        add(tabs);
    }

    // Methods to update Tables
    public void addPatientRow(Object[] row) { patientModel.addRow(row); }
    public void addReferralRow(Object[] row) { referralModel.addRow(row); }
    public void addPrescriptionRow(Object[] row) { prescriptionModel.addRow(row); }

    // Listeners
    public void setAddPatientListener(ActionListener al) { btnAddPatient.addActionListener(al); }
    public void setAddReferralListener(ActionListener al) { btnAddReferral.addActionListener(al); }
    public void setAddPrescriptionListener(ActionListener al) { btnAddPrescription.addActionListener(al); }
}