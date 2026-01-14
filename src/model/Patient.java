package model;

import util.CSVUtils;

public class Patient extends Person {
    private String id;
    private String dob;
    private String nhsNumber;
    private String gender;
    private String address;
    private String email;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String postcode;

    public Patient(String id, String fName, String lName, String dob, String nhs, String phone, String email) {
        super(fName, lName, phone);
        this.id = id;
        this.dob = dob;
        this.nhsNumber = nhs;
        this.email = email;
        this.gender = "Unknown";
        this.address = "";
        this.emergencyContact = "";
        this.emergencyContactPhone = "";
        this.postcode = "";
    }

    public Patient(String id, String fName, String lName, String dob, String nhs, String gender, String phone, String email, String address, String postcode, String emergencyContact, String emergencyContactPhone) {
        super(fName, lName, phone);
        this.id = id;
        this.dob = dob;
        this.nhsNumber = nhs;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.emergencyContact = emergencyContact;
        this.emergencyContactPhone = emergencyContactPhone;
        this.postcode = postcode;
    }

    public String getId() { return id; }
    public String getDob() { return dob; }
    public String getNhsNumber() { return nhsNumber; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getEmergencyContact() { return emergencyContact; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getPostcode() { return postcode; }

    public String toCSV() {
        return String.join(",",
                id, firstName, lastName, dob, nhsNumber, gender, phone, email,
                CSVUtils.toCSVField(address), postcode, emergencyContact, emergencyContactPhone, "2025-01-01", "S001"
        );
    }
}