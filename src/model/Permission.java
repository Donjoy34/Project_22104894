package model;

public class Permission {
    private String roleId;
    private String action;
    private boolean allowed;

    public Permission(String roleId, String action, boolean allowed) {
        this.roleId = roleId;
        this.action = action;
        this.allowed = allowed;
    }

    public String getRoleId() { return roleId; }
    public String getAction() { return action; }
    public boolean isAllowed() { return allowed; }
}
