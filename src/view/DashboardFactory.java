package view;

import model.User;
import model.DataManager;
import util.AccessControl;
import javax.swing.*;
import java.awt.*;

public class DashboardFactory {
    
    public static JPanel createDashboard(User user, DataManager dataManager) {
        switch(user.getRole()) {
            case PATIENT:
                return new PatientDashboard(user, dataManager);
            case GP:
                return new GPDashboard(user, dataManager);
            case SPECIALIST:
                return new SpecialistDashboard(user, dataManager);
            case NURSE:
                return new NurseDashboard(user, dataManager);
            case RECEPTIONIST:
                return new ReceptionistDashboard(user, dataManager);
            case ADMIN:
                return new AdminDashboard(user, dataManager);
            default:
                return new JPanel();
        }
    }
}
