package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {
    private String id;
    private String userId;
    private String action;
    private LocalDateTime timestamp;
    private String details;

    public AuditLog(String id, String userId, String action, LocalDateTime timestamp, String details) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDetails() { return details; }

    public String toCSV() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return id + "," + userId + "," + action + "," + timestamp.format(formatter) + "," + details;
    }

    public static AuditLog fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 5) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(parts[3], formatter);
            String details = parts.length > 4 ? parts[4] : "";
            return new AuditLog(parts[0], parts[1], parts[2], timestamp, details);
        }
        return null;
    }
}