package util;

import java.util.HashMap;
import java.util.Map;
import view.UserRole;

public class AccessControl {
    private static final Map<UserRole, String[]> ROLE_PERMISSIONS = new HashMap<>();

    static {
        // Patient permissions
        ROLE_PERMISSIONS.put(UserRole.PATIENT, new String[]{
            "BOOK_APPOINTMENT", "VIEW_APPOINTMENTS", "CANCEL_APPOINTMENT", 
            "VIEW_PRESCRIPTIONS", "VIEW_REFERRALS", "VIEW_PROFILE"
        });

        // GP permissions
        ROLE_PERMISSIONS.put(UserRole.GP, new String[]{
            "VIEW_APPOINTMENTS", "SEARCH_PATIENT", "CREATE_PRESCRIPTION", 
            "CREATE_REFERRAL", "UPDATE_NOTES", "VIEW_PATIENT_HISTORY"
        });

        // Specialist permissions
        ROLE_PERMISSIONS.put(UserRole.SPECIALIST, new String[]{
            "VIEW_REFERRALS", "ACCEPT_REFERRAL", "VIEW_PATIENT_HISTORY", 
            "CREATE_PRESCRIPTION", "ADD_TREATMENT_NOTES"
        });

        // Nurse permissions
        ROLE_PERMISSIONS.put(UserRole.NURSE, new String[]{
            "VIEW_PATIENTS", "RECORD_VITALS", "UPDATE_NOTES", "VIEW_SCHEDULE"
        });

        // Receptionist permissions
        ROLE_PERMISSIONS.put(UserRole.RECEPTIONIST, new String[]{
            "REGISTER_PATIENT", "BOOK_APPOINTMENT", "RESCHEDULE_APPOINTMENT", 
            "CANCEL_APPOINTMENT", "CHECK_IN_PATIENT"
        });

        // Admin permissions
        ROLE_PERMISSIONS.put(UserRole.ADMIN, new String[]{
            "MANAGE_USERS", "ASSIGN_ROLES", "VIEW_LOGS", "SYSTEM_SETTINGS", "DATA_BACKUP"
        });
    }

    public static boolean hasPermission(UserRole role, String action) {
        String[] permissions = ROLE_PERMISSIONS.get(role);
        if (permissions == null) return false;
        for (String perm : permissions) {
            if (perm.equals(action)) return true;
        }
        return false;
    }

    public static String[] getPermissions(UserRole role) {
        return ROLE_PERMISSIONS.getOrDefault(role, new String[]{});
    }
}
