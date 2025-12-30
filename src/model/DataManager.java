package model;

import util.CSVUtils;
import java.io.*;
import java.util.*;

public class DataManager {
    private List<Patient> patients = new ArrayList<>();
    private List<Referral> referrals = new ArrayList<>();
    private List<Prescription> prescriptions = new ArrayList<>();

    private final String DB_PATH = "database/";

    public void loadData() {
        loadPatients();
        loadReferrals();
        loadPrescriptions();
    }

    private void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "patients.csv"))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 2) {
                    patients.add(new Patient(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(6), data.get(8)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadReferrals() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "referrals.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 9) {
                    referrals.add(new Referral(data.get(0), data.get(1), data.get(2), data.get(7), data.get(9), data.get(11)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadPrescriptions() {
        try (BufferedReader br = new BufferedReader(new FileReader(DB_PATH + "prescriptions.csv"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                List<String> data = CSVUtils.parseLine(line);
                if(data.size() > 10) {
                    prescriptions.add(new Prescription(data.get(0), data.get(1), data.get(5), data.get(6), data.get(10)));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- Persist Methods ---

    public void addPatient(Patient p) {
        patients.add(p);
        appendToFile("patients.csv", p.toCSV());
    }

    public void addReferral(Referral r) {
        referrals.add(r);
        appendToFile("referrals.csv", r.toCSV());
    }

    public void addPrescription(Prescription p) {
        prescriptions.add(p);
        appendToFile("prescriptions.csv", p.toCSV());
    }

    private void appendToFile(String filename, String data) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DB_PATH + filename, true))) {
            pw.println(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Patient> getPatients() { return patients; }
    public List<Referral> getReferrals() { return referrals; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
}