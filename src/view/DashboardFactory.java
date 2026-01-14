package view;

import model.User;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class DashboardFactory {
    
    public static JPanel createDashboard(User user) {
        switch(user.getRole()) {
            case PATIENT:
                return new PatientDashboard(user);
            case GP:
                return new GPDashboard(user);
            case SPECIALIST:
                return new SpecialistDashboard(user);
            case NURSE:
                return new NurseDashboard(user);
            case RECEPTIONIST:
                return new ReceptionistDashboard(user);
            case ADMIN:
                return new AdminDashboard(user);
            default:
                return new JPanel();
        }
    }
}
