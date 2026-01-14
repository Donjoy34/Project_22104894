package view;

import model.User;
import model.DataManager;
import model.Patient;
import model.Appointment;
import model.Staff;
import model.Clinician;
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
        JLabel title = new JLabel("Receptionist Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel - 2x3 grid
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCreatePatient = new JButton("Create Patient");
        btnCreatePatient.addActionListener(e -> createPatientDialog());
        buttonPanel.add(btnCreatePatient);

        JButton btnViewPatients = new JButton("View Patients");
        btnViewPatients.addActionListener(e -> viewPatientsDialog());
        buttonPanel.add(btnViewPatients);

        JButton btnViewStaff = new JButton("View Staff");
        btnViewStaff.addActionListener(e -> viewStaffDialog());
        buttonPanel.add(btnViewStaff);

        JButton btnViewClinicians = new JButton("View Clinicians");
        btnViewClinicians.addActionListener(e -> viewCliniciansDialog());
        buttonPanel.add(btnViewClinicians);

        JButton btnViewAppointments = new JButton("View Appointments");
        btnViewAppointments.addActionListener(e -> viewAppointmentsDialog());
        buttonPanel.add(btnViewAppointments);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void createPatientDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Create Patient", true);
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(new JLabel("First Name:"));
        JTextField firstNameField = new JTextField();
        contentPanel.add(firstNameField);

        contentPanel.add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField();
        contentPanel.add(lastNameField);

        contentPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        JTextField dobField = new JTextField("1990-01-01");
        contentPanel.add(dobField);

        contentPanel.add(new JLabel("NHS Number:"));
        JTextField nhsNumberField = new JTextField();
        contentPanel.add(nhsNumberField);

        contentPanel.add(new JLabel("Gender:"));
        String[] genders = {"M", "F", "Other"};
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        contentPanel.add(genderCombo);

        contentPanel.add(new JLabel("Phone Number:"));
        JTextField phoneField = new JTextField();
        contentPanel.add(phoneField);

        contentPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        contentPanel.add(emailField);

        contentPanel.add(new JLabel("Address:"));
        JTextField addressField = new JTextField();
        contentPanel.add(addressField);

        contentPanel.add(new JLabel("Postcode:"));
        JTextField postcodeField = new JTextField();
        contentPanel.add(postcodeField);

        contentPanel.add(new JLabel("Emergency Contact Name:"));
        JTextField emergencyNameField = new JTextField();
        contentPanel.add(emergencyNameField);

        contentPanel.add(new JLabel("Emergency Contact Phone:"));
        JTextField emergencyPhoneField = new JTextField();
        contentPanel.add(emergencyPhoneField);

        contentPanel.add(new JLabel("GP Surgery ID:"));
        JTextField gpSurgeryField = new JTextField("S001");
        contentPanel.add(gpSurgeryField);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save Patient");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || 
                    nhsNumberField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields (First Name, Last Name, NHS Number)");
                    return;
                }

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
                                                (String) genderCombo.getSelectedItem(),
                                                phoneField.getText(), emailField.getText(), 
                                                addressField.getText(), postcodeField.getText(),
                                                emergencyNameField.getText(), emergencyPhoneField.getText());
                dataManager.addPatient(newPatient);
                
                JOptionPane.showMessageDialog(dialog, "Patient created successfully!\nPatient ID: " + patientId);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewPatientsDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "View Patients", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        List<Patient> patients = dataManager.getPatients();
        String[] columnNames = {"Patient ID", "First Name", "Last Name", "NHS Number", "DOB", "Gender", "Phone", "Email", "Address", "Postcode"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Patient p : patients) {
            model.addRow(new Object[]{
                p.getId(), p.getFirstName(), p.getLastName(), p.getNhsNumber(), 
                p.getDob(), p.getGender(), p.getPhone(), p.getEmail(), 
                p.getAddress(), p.getPostcode()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewStaffDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "View Staff", true);
        dialog.setSize(1000, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        List<Staff> staffList = dataManager.getStaffs();
        String[] columnNames = {"Staff ID", "First Name", "Last Name", "Role", "Department", "Facility ID", "Phone", "Email", "Employment Status", "Start Date", "Line Manager", "Access Level"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Staff s : staffList) {
            model.addRow(new Object[]{
                s.getId(), s.getFirstName(), s.getLastName(), s.getRole(), 
                s.getDepartment(), s.getFacilityId(), s.getPhone(), s.getEmail(), 
                s.getEmploymentStatus(), s.getStartDate(), s.getLineManager(), s.getAccessLevel()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewCliniciansDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "View Clinicians", true);
        dialog.setSize(1000, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        List<Clinician> clinicians = dataManager.getClinicians();
        String[] columnNames = {"Clinician ID", "First Name", "Last Name", "Title", "Specialty", "GMC Number", "Phone", "Email", "Workplace ID", "Workplace Type", "Employment Status", "Start Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Clinician c : clinicians) {
            model.addRow(new Object[]{
                c.getId(), c.getFirstName(), c.getLastName(), c.getTitle(), 
                c.getSpecialty(), c.getGmcNumber(), c.getPhone(), c.getEmail(), 
                c.getWorkplaceId(), c.getWorkplaceType(), c.getEmploymentStatus(), c.getStartDate()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void viewAppointmentsDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "View Appointments", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.add(new JLabel("Filter by Patient NHS Number:"));
        JComboBox<String> nhsCombo = new JComboBox<>();
        
        List<Patient> patients = dataManager.getPatients();
        nhsCombo.addItem("All Patients");
        for (Patient p : patients) {
            nhsCombo.addItem(p.getNhsNumber() + " - " + p.getFirstName() + " " + p.getLastName());
        }
        
        filterPanel.add(nhsCombo);
        dialog.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"Appointment ID", "Patient ID", "Provider ID", "Date/Time", "Status", "Notes"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        nhsCombo.addActionListener(e -> {
            model.setRowCount(0);
            List<Appointment> appointments = dataManager.getAppointments();
            
            if (nhsCombo.getSelectedIndex() == 0) {
                // Show all appointments
                for (Appointment a : appointments) {
                    model.addRow(new Object[]{
                        a.getId(), a.getPatientId(), a.getProviderId(), 
                        a.getAppointmentTime(), a.getStatus(), a.getNotes()
                    });
                }
            } else {
                // Filter by selected NHS number
                String selectedNHS = nhsCombo.getSelectedItem().toString().split(" - ")[0];
                Patient selectedPatient = null;
                for (Patient p : patients) {
                    if (p.getNhsNumber().equals(selectedNHS)) {
                        selectedPatient = p;
                        break;
                    }
                }
                
                if (selectedPatient != null) {
                    for (Appointment a : appointments) {
                        if (a.getPatientId().equals(selectedPatient.getId())) {
                            model.addRow(new Object[]{
                                a.getId(), a.getPatientId(), a.getProviderId(), 
                                a.getAppointmentTime(), a.getStatus(), a.getNotes()
                            });
                        }
                    }
                }
            }
        });

        // Load all appointments initially
        List<Appointment> appointments = dataManager.getAppointments();
        for (Appointment a : appointments) {
            model.addRow(new Object[]{
                a.getId(), a.getPatientId(), a.getProviderId(), 
                a.getAppointmentTime(), a.getStatus(), a.getNotes()
            });
        }

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
