package view;

import model.User;
import model.DataManager;
import model.Appointment;
import model.Prescription;
import model.Referral;
import model.Patient;
import util.AccessControl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GPDashboard extends JPanel {
    private User gp;
    private DataManager dataManager;

    public GPDashboard(User gp, DataManager dataManager) {
        this.gp = gp;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("GP Dashboard - Dr. " + gp.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.GP, "VIEW_APPOINTMENTS")) {
            JButton btnViewAppointments = new JButton("View Appointments");
            btnViewAppointments.addActionListener(e -> viewAppointmentsDialog());
            buttonPanel.add(btnViewAppointments);
        }

        if (AccessControl.hasPermission(UserRole.GP, "SEARCH_PATIENT")) {
            JButton btnSearchPatient = new JButton("Search Patient");
            btnSearchPatient.addActionListener(e -> searchPatientDialog());
            buttonPanel.add(btnSearchPatient);
        }

        if (AccessControl.hasPermission(UserRole.GP, "CREATE_PRESCRIPTION")) {
            JButton btnCreatePrescription = new JButton("Create Prescription");
            btnCreatePrescription.addActionListener(e -> createPrescriptionDialog());
            buttonPanel.add(btnCreatePrescription);
        }

        if (AccessControl.hasPermission(UserRole.GP, "CREATE_REFERRAL")) {
            JButton btnCreateReferral = new JButton("Create Referral");
            btnCreateReferral.addActionListener(e -> createReferralDialog());
            buttonPanel.add(btnCreateReferral);
        }

        if (AccessControl.hasPermission(UserRole.GP, "UPDATE_NOTES")) {
            JButton btnUpdateNotes = new JButton("Update Clinical Notes");
            btnUpdateNotes.addActionListener(e -> updateNotesDialog());
            buttonPanel.add(btnUpdateNotes);
        }

        if (AccessControl.hasPermission(UserRole.GP, "VIEW_PATIENT_HISTORY")) {
            JButton btnViewHistory = new JButton("View Patient History");
            btnViewHistory.addActionListener(e -> viewPatientHistoryDialog());
            buttonPanel.add(btnViewHistory);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void viewAppointmentsDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Appointment ID");
        model.addColumn("Patient");
        model.addColumn("Date");
        model.addColumn("Time");
        model.addColumn("Status");

        for (Appointment a : allAppointments) {
            if (a.getStatus().equals("Scheduled")) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                model.addRow(new Object[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getAppointmentTime().format(dateFormatter),
                    a.getAppointmentTime().format(timeFormatter),
                    a.getStatus()
                });
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Your Schedule", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchPatientDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        String searchTerm = JOptionPane.showInputDialog(this, "Enter patient name or ID:", "");
        if (searchTerm == null || searchTerm.isEmpty()) return;

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Patient ID");
        model.addColumn("Name");
        model.addColumn("NHS Number");
        model.addColumn("Email");

        String lowerSearch = searchTerm.toLowerCase();
        for (Patient p : patients) {
            if (p.getId().toLowerCase().contains(lowerSearch) || 
                (p.getFirstName() + " " + p.getLastName()).toLowerCase().contains(lowerSearch)) {
                model.addRow(new Object[]{p.getId(), p.getFirstName() + " " + p.getLastName(), p.getNhsNumber(), p.getEmail()});
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createPrescriptionDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Create Prescription", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(4, 2, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Patient selection
        String[] patientNames = new String[patients.size()];
        String[] patientIds = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            patientNames[i] = patients.get(i).getFirstName() + " " + patients.get(i).getLastName();
            patientIds[i] = patients.get(i).getId();
        }

        contentPanel.add(new JLabel("Select Patient:"));
        JComboBox<String> patientCombo = new JComboBox<>(patientNames);
        contentPanel.add(patientCombo);

        contentPanel.add(new JLabel("Medication:"));
        JTextField medicationField = new JTextField("Paracetamol");
        contentPanel.add(medicationField);

        contentPanel.add(new JLabel("Dosage:"));
        JTextField dosageField = new JTextField("500mg");
        contentPanel.add(dosageField);

        contentPanel.add(new JLabel("Instructions:"));
        JTextField instructionsField = new JTextField("Take twice daily");
        contentPanel.add(instructionsField);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        createBtn.addActionListener(e -> {
            try {
                int selectedIndex = patientCombo.getSelectedIndex();
                String patientId = patientIds[selectedIndex];
                
                // Generate prescription ID
                int maxId = 0;
                for (Prescription p : dataManager.getPrescriptions()) {
                    try {
                        int id = Integer.parseInt(p.getId().substring(2));
                        if (id > maxId) maxId = id;
                    } catch (Exception ex) {}
                }
                String prescriptionId = "PR" + String.format("%04d", maxId + 1);

                Prescription newPrescription = new Prescription(prescriptionId, patientId, medicationField.getText(), 
                                                               dosageField.getText(), instructionsField.getText());
                dataManager.addPrescription(newPrescription);
                
                JOptionPane.showMessageDialog(dialog, "Prescription created successfully!\nID: " + prescriptionId);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void createReferralDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Create Referral", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(5, 2, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Patient selection
        String[] patientNames = new String[patients.size()];
        String[] patientIds = new String[patients.size()];
        for (int i = 0; i < patients.size(); i++) {
            patientNames[i] = patients.get(i).getFirstName() + " " + patients.get(i).getLastName();
            patientIds[i] = patients.get(i).getId();
        }

        contentPanel.add(new JLabel("Select Patient:"));
        JComboBox<String> patientCombo = new JComboBox<>(patientNames);
        contentPanel.add(patientCombo);

        // Specialty selection
        String[] specialties = {"Cardiology", "Neurology", "Orthopaedics", "Dermatology", "Gastroenterology"};
        contentPanel.add(new JLabel("Specialty:"));
        JComboBox<String> specialtyCombo = new JComboBox<>(specialties);
        contentPanel.add(specialtyCombo);

        // Urgency level
        String[] urgencies = {"Routine", "Urgent", "Emergency"};
        contentPanel.add(new JLabel("Urgency:"));
        JComboBox<String> urgencyCombo = new JComboBox<>(urgencies);
        contentPanel.add(urgencyCombo);

        contentPanel.add(new JLabel("Reason:"));
        JTextField reasonField = new JTextField("Medical consultation");
        contentPanel.add(reasonField);

        contentPanel.add(new JLabel("Summary:"));
        JTextArea summaryArea = new JTextArea(2, 20);
        contentPanel.add(new JScrollPane(summaryArea));

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createBtn = new JButton("Create");
        JButton cancelBtn = new JButton("Cancel");

        createBtn.addActionListener(e -> {
            try {
                int selectedIndex = patientCombo.getSelectedIndex();
                String patientId = patientIds[selectedIndex];
                
                // Generate referral ID
                int maxId = 0;
                for (Referral r : dataManager.getReferrals()) {
                    try {
                        int id = Integer.parseInt(r.getId().substring(2));
                        if (id > maxId) maxId = id;
                    } catch (Exception ex) {}
                }
                String referralId = "RF" + String.format("%04d", maxId + 1);

                Referral newReferral = new Referral(referralId, patientId, gp.getId(), 
                                                    (String) urgencyCombo.getSelectedItem(), 
                                                    summaryArea.getText(), "Pending");
                newReferral.setSpecialty((String) specialtyCombo.getSelectedItem());
                dataManager.addReferral(newReferral);
                
                JOptionPane.showMessageDialog(dialog, "Referral created successfully!\nID: " + referralId);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateNotesDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:", "");
        if (patientId == null || patientId.isEmpty()) return;

        JTextArea notesArea = new JTextArea(10, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Update Clinical Notes for " + patientId, 
                                                  JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && !notesArea.getText().isEmpty()) {
            // Save notes
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("database/clinical_notes.csv", true))) {
                pw.println(patientId + ",\"" + notesArea.getText().replace("\"", "\"\"") + "\"," + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Clinical notes updated successfully!");
        }
    }

    private void viewPatientHistoryDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:", "");
        if (patientId == null || patientId.isEmpty()) return;

        // Find patient
        Patient patient = null;
        for (Patient p : dataManager.getPatients()) {
            if (p.getId().equals(patientId)) {
                patient = p;
                break;
            }
        }

        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Patient not found!");
            return;
        }

        // Build history display
        StringBuilder history = new StringBuilder();
        history.append("Patient: ").append(patient.getFirstName()).append(" ").append(patient.getLastName()).append("\n");
        history.append("NHS Number: ").append(patient.getNhsNumber()).append("\n");
        history.append("DOB: ").append(patient.getDob()).append("\n\n");

        history.append("Prescriptions:\n");
        for (Prescription p : dataManager.getPrescriptions()) {
            if (p.getPatientId().equals(patientId)) {
                history.append("- ").append(p.getMedName()).append(" (").append(p.getDosage()).append(")\n");
            }
        }

        history.append("\nReferrals:\n");
        for (Referral r : dataManager.getReferrals()) {
            if (r.getPatientId().equals(patientId)) {
                history.append("- ").append(r.getSpecialty()).append(" (").append(r.getStatus()).append(")\n");
            }
        }

        JTextArea historyArea = new JTextArea(history.toString());
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Patient History - " + patientId, JOptionPane.INFORMATION_MESSAGE);
    }
}
