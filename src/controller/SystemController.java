package controller;

import model.*;
import view.MainView;
import singleton.ReferralManager;
import javax.swing.*;
import java.util.UUID;

public class SystemController {
    private MainView view;
    private DataManager model;

    public SystemController(MainView view, DataManager model) {
        this.view = view;
        this.model = model;

        // Initialize Data
        model.loadData();
        refreshViews();

        // Wire Buttons
        view.setAddPatientListener(e -> handleAddPatient());
        view.setAddReferralListener(e -> handleAddReferral());
        view.setAddPrescriptionListener(e -> handleAddPrescription());
    }

    private void refreshViews() {
        for(Patient p : model.getPatients()) {
            view.addPatientRow(new Object[]{p.getId(), p.getFullName(), p.getDob(), p.getNhsNumber(), "See Detail"});
        }
        for(Referral r : model.getReferrals()) {
            view.addReferralRow(new Object[]{r.getId(), r.getPatientId(), r.getUrgency(), r.getSummary(), "New"});
        }
        for(Prescription p : model.getPrescriptions()) {
            view.addPrescriptionRow(new Object[]{p.getId(), p.getPatientId(), p.getMedName(), p.getId()});
        }
    }

    private void handleAddPatient() {
        JTextField fName = new JTextField();
        JTextField lName = new JTextField();
        JTextField nhs = new JTextField();

        Object[] message = {"First Name:", fName, "Last Name:", lName, "NHS Number:", nhs};
        int option = JOptionPane.showConfirmDialog(view, message, "New Patient", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newId = "P" + (1000 + model.getPatients().size());
            Patient p = new Patient(newId, fName.getText(), lName.getText(), "1990-01-01", nhs.getText(), "00000", "Unknown Address");
            model.addPatient(p);
            view.addPatientRow(new Object[]{p.getId(), p.getFullName(), p.getDob(), p.getNhsNumber(), "See Detail"});
        }
    }

    private void handleAddReferral() {
        JTextField pId = new JTextField();
        String[] urgencies = {"Routine", "Urgent", "Two Week Wait"};
        JComboBox<String> urgencyBox = new JComboBox<>(urgencies);
        JTextArea summary = new JTextArea(5, 20);

        Object[] message = {"Patient ID:", pId, "Urgency:", urgencyBox, "Clinical Summary:", new JScrollPane(summary)};
        int option = JOptionPane.showConfirmDialog(view, message, "New Referral", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String newId = "R" + (1000 + model.getReferrals().size());
            Referral r = new Referral(newId, pId.getText(), "C001", (String)urgencyBox.getSelectedItem(), summary.getText(), "New");

            // 1. Update Model (CSV)
            model.addReferral(r);
            // 2. Process via Singleton (Logic + Email Gen)
            ReferralManager.getInstance().processReferral(r);
            // 3. Update View
            view.addReferralRow(new Object[]{r.getId(), r.getPatientId(), r.getUrgency(), r.getSummary(), "New"});

            JOptionPane.showMessageDialog(view, "Referral Processed & Email Generated.");
        }
    }

    private void handleAddPrescription() {
        // Implementation for prescription dialog...
        String newId = "RX" + (1000 + model.getPrescriptions().size());
        Prescription p = new Prescription(newId, "P001", "Amoxicillin", "500mg", "3 times a day");
        model.addPrescription(p);
        view.addPrescriptionRow(new Object[]{p.getId(), "P001", "Amoxicillin", "500mg"});
    }
}