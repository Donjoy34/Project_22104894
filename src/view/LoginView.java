package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private UserRole role;
    private MainView mainView;

    public LoginView(UserRole role, MainView mainView) {
        this.role = role;
        this.mainView = mainView;
        setTitle("Login - " + role);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Username:"));
        JTextField txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        JPasswordField txtPassword = new JPasswordField();
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            // Simple login check (placeholder)
            if (!txtUsername.getText().isEmpty() && txtPassword.getPassword().length > 0) {
                JOptionPane.showMessageDialog(this, "Login successful for " + role);
                dispose(); // Close login window
                mainView.switchToDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });
        add(btnLogin);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }
}