package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class NurseDashboard extends JPanel {
    private User nurse;

    public NurseDashboard(User nurse) {
        this.nurse = nurse;
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
            btnViewPatients.addActionListener(e -> JOptionPane.showMessageDialog(this, "Assigned Patients:\n- Patient A\n- Patient B"));
            buttonPanel.add(btnViewPatients);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "RECORD_VITALS")) {
            JButton btnRecordVitals = new JButton("Record Vitals");
            btnRecordVitals.addActionListener(e -> JOptionPane.showMessageDialog(this, "Record Vitals (BP, Temp, HR, Weight)"));
            buttonPanel.add(btnRecordVitals);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "UPDATE_NOTES")) {
            JButton btnUpdateNotes = new JButton("Update Nursing Notes");
            btnUpdateNotes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Update Nursing Notes functionality"));
            buttonPanel.add(btnUpdateNotes);
        }

        if (AccessControl.hasPermission(UserRole.NURSE, "VIEW_SCHEDULE")) {
            JButton btnViewSchedule = new JButton("View Schedule");
            btnViewSchedule.addActionListener(e -> JOptionPane.showMessageDialog(this, "Today's Schedule:\n- 09:00 - Vitals Check\n- 14:00 - Patient Assessment"));
            buttonPanel.add(btnViewSchedule);
        }

        add(buttonPanel, BorderLayout.CENTER);
    }
}
