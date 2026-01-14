package model;

public class Staff extends Person {
    private String id;
    private String role;
    private String department;
    private String facilityId;
    private String phoneNumber;
    private String email;
    private String employmentStatus;
    private String startDate;
    private String lineManager;
    private String accessLevel;

    public Staff(String id, String firstName, String lastName, String role, String department,
                 String facilityId, String phoneNumber, String email, String employmentStatus,
                 String startDate, String lineManager, String accessLevel) {
        super(firstName, lastName, phoneNumber);
        this.id = id;
        this.role = role;
        this.department = department;
        this.facilityId = facilityId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }

    public String getId() { return id; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getFacilityId() { return facilityId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }
    public String getLineManager() { return lineManager; }
    public String getAccessLevel() { return accessLevel; }

    public String toCSV() {
        return String.join(",",
            id, firstName, lastName, role, department, facilityId, phoneNumber,
            email, employmentStatus, startDate, lineManager, accessLevel
        );
    }
}
