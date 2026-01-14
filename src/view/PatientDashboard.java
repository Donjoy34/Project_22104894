package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class PatientDashboard extends JPanel {
    private User patient;

    public PatientDashboard(User patient) {
        this.patient = patient;
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
            btnBookAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Book Appointment functionality"));
            buttonPanel.add(btnBookAppointment);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_APPOINTMENTS")) {
            JButton btnViewAppointments = new JButton("View Appointments");
            btnViewAppointments.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your Appointments:\n- Appointment 1\n- Appointment 2"));
            buttonPanel.add(btnViewAppointments);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "CANCEL_APPOINTMENT")) {
            JButton btnCancelAppointment = new JButton("Cancel Appointment");
            btnCancelAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Cancel Appointment functionality"));
            buttonPanel.add(btnCancelAppointment);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_PRESCRIPTIONS")) {
            JButton btnViewPrescriptions = new JButton("View Prescriptions");
            btnViewPrescriptions.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your Prescriptions:\n- Paracetamol\n- Aspirin"));
            buttonPanel.add(btnViewPrescriptions);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_REFERRALS")) {
            JButton btnViewReferrals = new JButton("View Referrals");
            btnViewReferrals.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your Referrals:\n- Referral to Cardiologist"));
            buttonPanel.add(btnViewReferrals);
        }

        if (AccessControl.hasPermission(UserRole.PATIENT, "VIEW_PROFILE")) {
            JButton btnProfile = new JButton("View Profile");
            btnProfile.addActionListener(e -> JOptionPane.showMessageDialog(this, "Patient ID: " + patient.getId() + "\nName: " + patient.getUsername()));
            buttonPanel.add(btnProfile);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }
}
