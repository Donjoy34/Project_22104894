package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class ReceptionistDashboard extends JPanel {
    private User receptionist;

    public ReceptionistDashboard(User receptionist) {
        this.receptionist = receptionist;
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
            btnRegisterPatient.addActionListener(e -> JOptionPane.showMessageDialog(this, "Register Patient functionality"));
            buttonPanel.add(btnRegisterPatient);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "BOOK_APPOINTMENT")) {
            JButton btnBookAppointment = new JButton("Book Appointment");
            btnBookAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Book Appointment functionality"));
            buttonPanel.add(btnBookAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "RESCHEDULE_APPOINTMENT")) {
            JButton btnRescheduleAppointment = new JButton("Reschedule Appointment");
            btnRescheduleAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reschedule Appointment functionality"));
            buttonPanel.add(btnRescheduleAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "CANCEL_APPOINTMENT")) {
            JButton btnCancelAppointment = new JButton("Cancel Appointment");
            btnCancelAppointment.addActionListener(e -> JOptionPane.showMessageDialog(this, "Cancel Appointment functionality"));
            buttonPanel.add(btnCancelAppointment);
        }

        if (AccessControl.hasPermission(UserRole.RECEPTIONIST, "CHECK_IN_PATIENT")) {
            JButton btnCheckIn = new JButton("Check-In Patient");
            btnCheckIn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Patient Check-In functionality"));
            buttonPanel.add(btnCheckIn);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }
}
