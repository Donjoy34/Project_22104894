package model;

import util.CSVUtils;

public class Patient extends Person {
    private String id;
    private String dob;
    private String nhsNumber;
    private String address;
    private String email;

    public Patient(String id, String fName, String lName, String dob, String nhs, String phone, String email) {
        super(fName, lName, phone);
        this.id = id;
        this.dob = dob;
        this.nhsNumber = nhs;
        this.address = "";
        this.email = email;
    }

    public String getId() { return id; }
    public String getDob() { return dob; }
    public String getNhsNumber() { return nhsNumber; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }

    public String toCSV() {
        return String.join(",",
                id, firstName, lastName, dob, nhsNumber, "Unknown", phone, email,
                CSVUtils.toCSVField(address), "Postcode", "Contact", "000", "2025-01-01", "S001"
        );
    }
}