package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class GPDashboard extends JPanel {
    private User gp;

    public GPDashboard(User gp) {
        this.gp = gp;
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
            btnViewAppointments.addActionListener(e -> JOptionPane.showMessageDialog(this, "Your Schedule:\n- 10:00 AM - Patient A\n- 11:00 AM - Patient B"));
            buttonPanel.add(btnViewAppointments);
        }

        if (AccessControl.hasPermission(UserRole.GP, "SEARCH_PATIENT")) {
            JButton btnSearchPatient = new JButton("Search Patient");
            btnSearchPatient.addActionListener(e -> JOptionPane.showMessageDialog(this, "Search Patient functionality"));
            buttonPanel.add(btnSearchPatient);
        }

        if (AccessControl.hasPermission(UserRole.GP, "CREATE_PRESCRIPTION")) {
            JButton btnCreatePrescription = new JButton("Create Prescription");
            btnCreatePrescription.addActionListener(e -> JOptionPane.showMessageDialog(this, "Create Prescription functionality"));
            buttonPanel.add(btnCreatePrescription);
        }

        if (AccessControl.hasPermission(UserRole.GP, "CREATE_REFERRAL")) {
            JButton btnCreateReferral = new JButton("Create Referral");
            btnCreateReferral.addActionListener(e -> JOptionPane.showMessageDialog(this, "Create Referral functionality"));
            buttonPanel.add(btnCreateReferral);
        }

        if (AccessControl.hasPermission(UserRole.GP, "UPDATE_NOTES")) {
            JButton btnUpdateNotes = new JButton("Update Clinical Notes");
            btnUpdateNotes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update Notes functionality"));
            buttonPanel.add(btnUpdateNotes);
        }

        if (AccessControl.hasPermission(UserRole.GP, "VIEW_PATIENT_HISTORY")) {
            JButton btnViewHistory = new JButton("View Patient History");
            btnViewHistory.addActionListener(e -> JOptionPane.showMessageDialog(this, "Patient History functionality"));
            buttonPanel.add(btnViewHistory);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }
}
