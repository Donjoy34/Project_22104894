package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class SpecialistDashboard extends JPanel {
    private User specialist;

    public SpecialistDashboard(User specialist) {
        this.specialist = specialist;
        setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Specialist Dashboard - Dr. " + specialist.getUsername(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnViewReferrals = new JButton("View Referrals");
        btnViewReferrals.addActionListener(e -> JOptionPane.showMessageDialog(this, "Incoming Referrals:\n- Cardiology Referral\n- Neurology Referral"));
        buttonPanel.add(btnViewReferrals);

        JButton btnAcceptReferral = new JButton("Accept/Reject Referral");
        btnAcceptReferral.addActionListener(e -> JOptionPane.showMessageDialog(this, "Accept/Reject Referral functionality"));
        buttonPanel.add(btnAcceptReferral);

        JButton btnViewPatientHistory = new JButton("View Patient History");
        btnViewPatientHistory.addActionListener(e -> JOptionPane.showMessageDialog(this, "Patient History functionality"));
        buttonPanel.add(btnViewPatientHistory);

        JButton btnCreatePrescription = new JButton("Create Prescription");
        btnCreatePrescription.addActionListener(e -> JOptionPane.showMessageDialog(this, "Create Prescription functionality"));
        buttonPanel.add(btnCreatePrescription);

        JButton btnAddTreatmentNotes = new JButton("Add Treatment Notes");
        btnAddTreatmentNotes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add Treatment Notes functionality"));
        buttonPanel.add(btnAddTreatmentNotes);

        add(buttonPanel, BorderLayout.CENTER);
    }
}
