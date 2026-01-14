package model;

public class Staff {
    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private String facilityId;
    private String phone;
    private String email;
    private String employmentStatus;
    private String startDate;
    private String lineManager;
    private String accessLevel;

    public Staff(String id, String firstName, String lastName, String role, String department,
                 String facilityId, String phone, String email, String employmentStatus,
                 String startDate, String lineManager, String accessLevel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.department = department;
        this.facilityId = facilityId;
        this.phone = phone;
        this.email = email;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
    public String getDepartment() { return department; }
    public String getFacilityId() { return facilityId; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }
    public String getLineManager() { return lineManager; }
    public String getAccessLevel() { return accessLevel; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setRole(String role) { this.role = role; }
    public void setDepartment(String department) { this.department = department; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setLineManager(String lineManager) { this.lineManager = lineManager; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }

    public String toCSV() {
        return id + "," + firstName + "," + lastName + "," + role + "," + department + "," +
               facilityId + "," + phone + "," + email + "," + employmentStatus + "," +
               startDate + "," + lineManager + "," + accessLevel;
    }
}
