package model;

public class Clinician extends Person {
    private String id;
    private String title;
    private String specialty;
    private String gmcNumber;
    private String phoneNumber;
    private String email;
    private String workplaceId;
    private String workplaceType;
    private String employmentStatus;
    private String startDate;

    public Clinician(String id, String firstName, String lastName, String title, String specialty,
                     String gmcNumber, String phoneNumber, String email, String workplaceId,
                     String workplaceType, String employmentStatus, String startDate) {
        super(firstName, lastName, phoneNumber);
        this.id = id;
        this.title = title;
        this.specialty = specialty;
        this.gmcNumber = gmcNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSpecialty() { return specialty; }
    public String getGmcNumber() { return gmcNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getWorkplaceId() { return workplaceId; }
    public String getWorkplaceType() { return workplaceType; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }

    public String toCSV() {
        return String.join(",",
            id, firstName, lastName, title, specialty, gmcNumber, phoneNumber,
            email, workplaceId, workplaceType, employmentStatus, startDate
        );
    }
}
