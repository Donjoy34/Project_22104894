package model;

public class Clinician {
    private String id;
    private String firstName;
    private String lastName;
    private String title;
    private String specialty;
    private String gmcNumber;
    private String phone;
    private String email;
    private String workplaceId;
    private String workplaceType;
    private String employmentStatus;
    private String startDate;

    public Clinician(String id, String firstName, String lastName, String title, String specialty,
                     String gmcNumber, String phone, String email, String workplaceId,
                     String workplaceType, String employmentStatus, String startDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.specialty = specialty;
        this.gmcNumber = gmcNumber;
        this.phone = phone;
        this.email = email;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getTitle() { return title; }
    public String getSpecialty() { return specialty; }
    public String getGmcNumber() { return gmcNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWorkplaceId() { return workplaceId; }
    public String getWorkplaceType() { return workplaceType; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setTitle(String title) { this.title = title; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setGmcNumber(String gmcNumber) { this.gmcNumber = gmcNumber; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setWorkplaceId(String workplaceId) { this.workplaceId = workplaceId; }
    public void setWorkplaceType(String workplaceType) { this.workplaceType = workplaceType; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String toCSV() {
        return id + "," + firstName + "," + lastName + "," + title + "," + specialty + "," +
               gmcNumber + "," + phone + "," + email + "," + workplaceId + "," +
               workplaceType + "," + employmentStatus + "," + startDate;
    }
}
