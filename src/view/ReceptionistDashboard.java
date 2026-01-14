package view;

import model.User;
import model.DataManager;
import model.Patient;
import model.Appointment;
import util.AccessControl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceptionistDashboard extends JPanel {
    private User receptionist;
    private DataManager dataManager;

    public ReceptionistDashboard(User receptionist, DataManager dataManager) {
        this.receptionist = receptionist;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Receptionist Dashboard - " + receptionist.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "REGISTER_PATIENT")) {
            JButton btnRegisterPatient = new JButton("Register New Patient");
            btnRegisterPatient.addActionListener(e -> registerPatientDialog());
            buttonPanel.add(btnRegisterPatient);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "BOOK_APPOINTMENT")) {
            JButton btnBookAppointment = new JButton("Book Appointment");
            btnBookAppointment.addActionListener(e -> bookAppointmentDialog());
            buttonPanel.add(btnBookAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "RESCHEDULE_APPOINTMENT")) {
            JButton btnRescheduleAppointment = new JButton("Reschedule Appointment");
            btnRescheduleAppointment.addActionListener(e -> rescheduleAppointmentDialog());
            buttonPanel.add(btnRescheduleAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "CANCEL_APPOINTMENT")) {
            JButton btnCancelAppointment = new JButton("Cancel Appointment");
            btnCancelAppointment.addActionListener(e -> cancelAppointmentDialog());
            buttonPanel.add(btnCancelAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "CHECK_IN_PATIENT")) {
            JButton btnCheckIn = new JButton("Check-In Patient");
            btnCheckIn.addActionListener(e -> checkInPatientDialog());
            buttonPanel.add(btnCheckIn);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void registerPatientDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Register New Patient", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(7, 2, 5, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("First Name:"));
        JTextField firstNameField = new JTextField();
        contentPanel.add(firstNameField);

        contentPanel.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField();
        contentPanel.add(lastNameField);

        contentPanel.add(new JLabel("NHS Number:"));
        JTextField nhsNumberField = new JTextField();
        contentPanel.add(nhsNumberField);

        contentPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        contentPanel.add(emailField);

        contentPanel.add(new JLabel("Phone:"));
        JTextField phoneField = new JTextField();
        contentPanel.add(phoneField);

        contentPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField("1990-01-01");
        contentPanel.add(dobField);

        contentPanel.add(new JLabel("Address:"));
        JTextField addressField = new JTextField();
        contentPanel.add(addressField);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton registerBtn = new JButton("Register");
        JButton cancelBtn = new JButton("Cancel");

        registerBtn.addActionListener(e -> {
            try {
                // Generate patient ID
                List<Patient> patients = dataManager.getPatients();
                int maxId = 0;
                for (Patient p : patients) {
                    try {
                        int id = Integer.parseInt(p.getId().substring(1));
                        if (id > maxId) maxId = id;
                    } catch (Exception ex) {}
                }
                String patientId = "P" + String.format("%03d", maxId + 1);

                Patient newPatient = new Patient(patientId, firstNameField.getText(), lastNameField.getText(), 
                                                dobField.getText(), nhsNumberField.getText(), 
                                                phoneField.getText(), emailField.getText());
                dataManager.addPatient(newPatient);
                
                JOptionPane.showMessageDialog(dialog, "Patient registered successfully!\nPatient ID: " + patientId);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(registerBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void bookAppointmentDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dialog.setSize(400, 300);
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

        // Clinician selection
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

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirmBtn = new JButton("Book");
        JButton cancelBtn = new JButton("Cancel");

        confirmBtn.addActionListener(e -> {
            try {
                int patientIndex = patientCombo.getSelectedIndex();
                int clinicianIndex = clinicianCombo.getSelectedIndex();
                String patientId = patientIds[patientIndex];
                String providerId = clinicianIds[clinicianIndex];
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

                Appointment newAppointment = new Appointment(appointmentId, patientId, providerId, 
                                                             appointmentTime, "Scheduled", "");
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

    private void rescheduleAppointmentDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        java.util.List<Appointment> appointments = new java.util.ArrayList<>();
        java.util.List<String> appointmentDisplay = new java.util.ArrayList<>();

        for (Appointment a : allAppointments) {
            if (a.getStatus().equals("Scheduled")) {
                appointments.add(a);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                appointmentDisplay.add(a.getId() + " - " + a.getPatientId() + " - " + a.getAppointmentTime().format(formatter));
            }
        }

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments available to reschedule");
            return;
        }

        String[] options = appointmentDisplay.toArray(new String[0]);
        int selectedIndex = JOptionPane.showOptionDialog(this, "Select appointment to reschedule:", 
                                                        "Reschedule Appointment", JOptionPane.DEFAULT_OPTION, 
                                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedIndex >= 0) {
            Appointment selectedAppt = appointments.get(selectedIndex);
            
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Reschedule Appointment", true);
            dialog.setSize(350, 200);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout(10, 10));

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new GridLayout(2, 2, 5, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            contentPanel.add(new JLabel("New Date:"));
            JTextField dateField = new JTextField("2025-01-20");
            contentPanel.add(dateField);

            contentPanel.add(new JLabel("New Time:"));
            JTextField timeField = new JTextField("14:00");
            contentPanel.add(timeField);

            dialog.add(contentPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton confirmBtn = new JButton("Reschedule");
            JButton cancelBtn = new JButton("Cancel");

            confirmBtn.addActionListener(e -> {
                try {
                    LocalDate date = LocalDate.parse(dateField.getText());
                    LocalTime time = LocalTime.parse(timeField.getText());
                    LocalDateTime newTime = LocalDateTime.of(date, time);
                    selectedAppt.setAppointmentTime(newTime);
                    dataManager.rewriteAppointmentsCSV();
                    JOptionPane.showMessageDialog(dialog, "Appointment rescheduled successfully!");
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
    }

    private void cancelAppointmentDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        java.util.List<Appointment> appointments = new java.util.ArrayList<>();
        java.util.List<String> appointmentDisplay = new java.util.ArrayList<>();

        for (Appointment a : allAppointments) {
            if (a.getStatus().equals("Scheduled")) {
                appointments.add(a);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                appointmentDisplay.add(a.getId() + " - " + a.getPatientId() + " - " + a.getAppointmentTime().format(formatter));
            }
        }

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No appointments to cancel");
            return;
        }

        String[] options = appointmentDisplay.toArray(new String[0]);
        int selectedIndex = JOptionPane.showOptionDialog(this, "Select appointment to cancel:", 
                                                        "Cancel Appointment", JOptionPane.DEFAULT_OPTION, 
                                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedIndex >= 0) {
            dataManager.cancelAppointment(appointments.get(selectedIndex).getId());
            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
        }
    }

    private void checkInPatientDialog() {
        List<Appointment> allAppointments = dataManager.getAppointments();
        java.util.List<Appointment> appointments = new java.util.ArrayList<>();
        java.util.List<String> appointmentDisplay = new java.util.ArrayList<>();

        for (Appointment a : allAppointments) {
            if (a.getStatus().equals("Scheduled")) {
                appointments.add(a);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                appointmentDisplay.add(a.getId() + " - " + a.getPatientId() + " - " + a.getAppointmentTime().format(formatter));
            }
        }

        if (appointments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No patients to check in");
            return;
        }

        String[] options = appointmentDisplay.toArray(new String[0]);
        int selectedIndex = JOptionPane.showOptionDialog(this, "Select patient to check in:", 
                                                        "Check-In Patient", JOptionPane.DEFAULT_OPTION, 
                                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (selectedIndex >= 0) {
            Appointment appt = appointments.get(selectedIndex);
            appt.setStatus("CheckedIn");
            dataManager.rewriteAppointmentsCSV();
            JOptionPane.showMessageDialog(this, "Patient checked in successfully!\nPatient: " + appt.getPatientId());
        }
    }
}
