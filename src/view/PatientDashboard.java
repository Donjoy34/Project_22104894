package view;

import model.*;
import util.AccessControl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientDashboard extends JPanel {
    private User patient;
    private DataManager dataManager;

    public PatientDashboard(User patient, DataManager dataManager) {
        this.patient = patient;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Patient Dashboard - " + patient.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.PATIENT, "BOOK_APPOINTMENT")) {
            JButton btnBookAppointment = new JButton("Book Appointment");
            btnBookAppointment.addActionListener(e -> bookAppointmentDialog());
            buttonPanel.add(btnBookAppointment);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_APPOINTMENTS")) {
            JButton btnViewAppointments = new JButton("View Appointments");
            btnViewAppointments.addActionListener(e -> viewAppointmentsDialog());
            buttonPanel.add(btnViewAppointments);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "CANCEL_APPOINTMENT")) {
            JButton btnCancelAppointment = new JButton("Cancel Appointment");
            btnCancelAppointment.addActionListener(e -> cancelAppointmentDialog());
            buttonPanel.add(btnCancelAppointment);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_PRESCRIPTIONS")) {
            JButton btnViewPrescriptions = new JButton("View Prescriptions");
            btnViewPrescriptions.addActionListener(e -> viewPrescriptionsDialog());
            buttonPanel.add(btnViewPrescriptions);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_REFERRALS")) {
            JButton btnViewReferrals = new JButton("View Referrals");
            btnViewReferrals.addActionListener(e -> viewReferralsDialog());
            buttonPanel.add(btnViewReferrals);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_PROFILE")) {
            JButton btnProfile = new JButton("View Profile");
            btnProfile.addActionListener(e -> viewProfileDialog());
            buttonPanel.add(btnProfile);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void bookAppointmentDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(4, 2, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Get available clinicians
        String[] clinicianNames = {"Dr. David Thompson (GP)", "Dr. Helen Roberts (GP)", "Dr. Mark Davies (GP)", 
                                   "Dr. Susan Clarke (GP)", "Dr. Richard Evans (Cardiology)", "Dr. Maria Rodriguez (Neurology)"};
        String[] clinicianIds = {"C001", "C002", "C003", "C004", "C005", "C006"};

        contentPanel.add(new JLabel("Select Clinician:"));
        JComboBox<String> clinicianCombo = new JComboBox<>(clinicianNames);
        contentPanel.add(clinicianCombo);

        contentPanel.add(new JLabel("Appointment Date:"));
        JTextField dateField = new JTextField("2025-01-20");
        contentPanel.add(dateField);

        contentPanel.add(new JLabel("Appointment Time:"));
        JTextField timeField = new JTextField("14:00");
        contentPanel.add(timeField);

        contentPanel.add(new JLabel("Reason for Visit:"));
        JTextField reasonField = new JTextField("General Consultation");
        contentPanel.add(reasonField);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmBtn = new JButton("Book");
        JButton cancelBtn = new JButton("Cancel");

        confirmBtn.addActionListener(e -> {
            try {
                int selectedIndex = clinicianCombo.getSelectedIndex();
                String providerId = clinicianIds[selectedIndex];
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime time = LocalTime.parse(timeField.getText());
                LocalDateTime appointmentTime = LocalDateTime.of(date, time);
                
                // Generate appointment ID
                int maxId = 0;
                for (Appointment a : dataManager.getAppointments()) {
                    try {
                        int id = Integer.parseInt(a.getId().substring(1));
                        if (id > maxId) maxId = id;
                    } catch (Exception ex) {}
                }
                String appointmentId = "A" + String.format("%03d", maxId + 1);

                Appointment newAppointment = new Appointment(appointmentId, patient.getId(), providerId, 
                                                             appointmentTime, "Scheduled", reasonField.getText());
                dataManager.addAppointment(newAppointment);
                
                JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!\nAppointment ID: " + appointmentId);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewAppointmentsDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        
        // Filter appointments for current user
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Appointment ID");
        model.addColumn("Date");
        model.addColumn("Time");
        model.addColumn("Provider");
        model.addColumn("Status");
        model.addColumn("Notes");

        for (Appointment a : allAppointments) {
            if (a.getPatientId().equals(patient.getId())) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                model.addRow(new Object[]{
                    a.getId(),
                    a.getAppointmentTime().format(dateFormatter),
                    a.getAppointmentTime().format(timeFormatter),
                    a.getProviderId(),
                    a.getStatus(),
                    a.getNotes()
                });
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JOptionPane.showMessageDialog(this, scrollPane, "Your Appointments", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelAppointmentDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        java.util.List<Appointment> userAppointments = new java.util.ArrayList<>();
        java.util.List<String> appointmentDisplay = new java.util.ArrayList<>();

        for (Appointment a : allAppointments) {
            if (a.getPatientId().equals(patient.getId()) && a.getStatus().equals("Scheduled")) {
                userAppointments.add(a);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                appointmentDisplay.add(a.getId() + " - " + a.getAppointmentTime().format(formatter));
            }
        }

        if (userAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No scheduled appointments to cancel");
            return;
        }

        String[] options = appointmentDisplay.toArray(new String[0]);
        int selectedIndex = JOptionPane.showOptionDialog(this, "Select appointment to cancel:", 
                                                        "Cancel Appointment", JOptionPane.DEFAULT_OPTION, 
                                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedIndex >= 0) {
            dataManager.cancelAppointment(userAppointments.get(selectedIndex).getId());
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
        }
    }

    private void viewPrescriptionsDialog() {
        List<Prescription> prescriptions = dataManager.getPrescriptions();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Prescription ID");
        model.addColumn("Medication");
        model.addColumn("Dosage");
        model.addColumn("Prescribed By");

        for (Prescription p : prescriptions) {
            if (p.getPatientId().equals(patient.getId())) {
                model.addRow(new Object[]{p.getId(), p.getMedication(), p.getDosage(), p.getClinicianId()});
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Your Prescriptions", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewReferralsDialog() {
        List<Referral> referrals = dataManager.getReferrals();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Referral ID");
        model.addColumn("Specialty");
        model.addColumn("Status");
        model.addColumn("Created Date");

        for (Referral r : referrals) {
            if (r.getPatientId().equals(patient.getId())) {
                model.addRow(new Object[]{r.getId(), r.getSpecialty(), r.getStatus(), r.getCreatedDate()});
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Your Referrals", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewProfileDialog() {
        Patient patientProfile = null;
        for (Patient p : dataManager.getPatients()) {
            if (p.getId().equals(patient.getId())) {
                patientProfile = p;
                break;
            }
        }

        if (patientProfile == null) {
            JOptionPane.showMessageDialog(this, "Patient profile not found");
            return;
        }

        String profileInfo = "Name: " + patientProfile.getFirstName() + " " + patientProfile.getLastName() + "\n" +
                           "Date of Birth: " + patientProfile.getDob() + "\n" +
                           "NHS Number: " + patientProfile.getNhsNumber() + "\n" +
                           "Email: " + patientProfile.getEmail() + "\n" +
                           "Phone: " + patientProfile.getPhone();

        JOptionPane.showMessageDialog(this, profileInfo, "Patient Profile", JOptionPane.INFORMATION_MESSAGE);
    }
}
