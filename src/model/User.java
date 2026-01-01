package model;

import view.UserRole;

public class User {
    private String id;
    private String username;
    private String passwordHash;
    private UserRole role;

    public User(String id, String username, String passwordHash, UserRole role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public UserRole getRole() { return role; }

    public String toCSV() {
        return id + "," + username + "," + passwordHash + "," + role;
    }
}