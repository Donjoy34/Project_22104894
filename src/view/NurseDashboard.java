package view;

import model.User;
import model.DataManager;
import model.Patient;
import model.Vitals;
import util.AccessControl;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NurseDashboard extends JPanel {
    private User nurse;
    private DataManager dataManager;

    public NurseDashboard(User nurse, DataManager dataManager) {
        this.nurse = nurse;
        this.dataManager = dataManager;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Nurse Dashboard - " + nurse.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (AccessControl.hasPermission(UserRole.NURSE, "VIEW_PATIENTS")) {
            JButton btnViewPatients = new JButton("View Assigned Patients");
            btnViewPatients.addActionListener(e -> viewPatientsDialog());
            buttonPanel.add(btnViewPatients);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "RECORD_VITALS")) {
            JButton btnRecordVitals = new JButton("Record Vitals");
            btnRecordVitals.addActionListener(e -> recordVitalsDialog());
            buttonPanel.add(btnRecordVitals);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "UPDATE_NOTES")) {
            JButton btnUpdateNotes = new JButton("Update Nursing Notes");
            btnUpdateNotes.addActionListener(e -> updateNotesDialog());
            buttonPanel.add(btnUpdateNotes);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "VIEW_SCHEDULE")) {
            JButton btnViewSchedule = new JButton("View Schedule");
            btnViewSchedule.addActionListener(e -> viewScheduleDialog());
            buttonPanel.add(btnViewSchedule);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void viewPatientsDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Patient ID");
        model.addColumn("Name");
        model.addColumn("NHS Number");
        model.addColumn("Email");

        for (Patient p : patients) {
            model.addRow(new Object[]{p.getId(), p.getFirstName() + " " + p.getLastName(), p.getNhsNumber(), p.getEmail()});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Assigned Patients", JOptionPane.INFORMATION_MESSAGE);
    }

    private void recordVitalsDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Record Vitals", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(6, 2, 5, 10));
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

        contentPanel.add(new JLabel("Blood Pressure (Systolic):"));
        JTextField bpSystolicField = new JTextField("120");
        contentPanel.add(bpSystolicField);

        contentPanel.add(new JLabel("Blood Pressure (Diastolic):"));
        JTextField bpDiastolicField = new JTextField("80");
        contentPanel.add(bpDiastolicField);

        contentPanel.add(new JLabel("Temperature (°C):"));
        JTextField temperatureField = new JTextField("37.0");
        contentPanel.add(temperatureField);

        contentPanel.add(new JLabel("Heart Rate (bpm):"));
        JTextField heartRateField = new JTextField("72");
        contentPanel.add(heartRateField);

        contentPanel.add(new JLabel("Weight (kg):"));
        JTextField weightField = new JTextField("70.0");
        contentPanel.add(weightField);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                int selectedIndex = patientCombo.getSelectedIndex();
                String patientId = patientIds[selectedIndex];
                
                int bpSystolic = Integer.parseInt(bpSystolicField.getText());
                int bpDiastolic = Integer.parseInt(bpDiastolicField.getText());
                double temperature = Double.parseDouble(temperatureField.getText());
                int heartRate = Integer.parseInt(heartRateField.getText());
                double weight = Double.parseDouble(weightField.getText());

                Vitals vitals = new Vitals(patientId, bpSystolic, bpDiastolic, temperature, heartRate, weight);
                
                // Save to CSV
                try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("database/vitals.csv", true))) {
                    pw.println(patientId + "," + bpSystolic + "," + bpDiastolic + "," + temperature + "," + heartRate + "," + weight + "," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(dialog, "Vitals recorded successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateNotesDialog() {
        List<Patient> patients = dataManager.getPatients();
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Update Nursing Notes", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(2, 1, 5, 10));
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

        contentPanel.add(new JLabel("Nursing Notes:"));
        JTextArea notesArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(notesArea);
        contentPanel.add(scrollPane);

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            int selectedIndex = patientCombo.getSelectedIndex();
            String patientId = patientIds[selectedIndex];
            String notes = notesArea.getText();
            
            // Save notes to nursing_notes.csv
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("database/nursing_notes.csv", true))) {
                pw.println(patientId + ",\"" + notes.replace("\"", "\"\"") + "\"," + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(dialog, "Notes saved successfully!");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewScheduleDialog() {
        List<model.Appointment> appointments = dataManager.getAppointments();
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Appointment ID");
        model.addColumn("Date");
        model.addColumn("Time");
        model.addColumn("Patient");
        model.addColumn("Status");

        for (model.Appointment a : appointments) {
            if (a.getStatus().equals("Scheduled")) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                model.addRow(new Object[]{
                    a.getId(),
                    a.getAppointmentTime().format(dateFormatter),
                    a.getAppointmentTime().format(timeFormatter),
                    a.getPatientId(),
                    a.getStatus()
                });
            }
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Today's Schedule", JOptionPane.INFORMATION_MESSAGE);
    }
}
