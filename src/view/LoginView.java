package view;

import model.DataManager;
import model.User;
import model.AuditLog;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class LoginView extends JFrame {
    private UserRole role;
    private MainView mainView;
    private DataManager model;

    public LoginView(UserRole role, MainView mainView, DataManager model) {
        this.role = role;
        this.mainView = mainView;
        this.model = model;
        setTitle("Login - " + role);
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("Username:"));
        JTextField txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Password:"));
        JPasswordField txtPassword = new JPasswordField();
        add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            User user = model.authenticate(username, password, role);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful for " + role);
                // Log login
                AuditLog loginLog = new AuditLog("A" + (1000 + model.getAuditLogs().size()), user.getId(), "LOGIN", LocalDateTime.now(), "User logged in");
                model.addAuditLog(loginLog);
                // Set current user
                mainView.setCurrentUser(user);
                dispose(); // Close login window
                mainView.switchToDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role mismatch");
            }
        });
        add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(e -> openRegisterDialog());
        add(btnRegister);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }

    private void openRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register", true);
        registerDialog.setSize(300, 200);
        registerDialog.setLayout(new GridLayout(4, 2, 10, 10));

        registerDialog.add(new JLabel("Username:"));
        JTextField txtRegUsername = new JTextField();
        registerDialog.add(txtRegUsername);

        registerDialog.add(new JLabel("Password:"));
        JPasswordField txtRegPassword = new JPasswordField();
        registerDialog.add(txtRegPassword);

        registerDialog.add(new JLabel("Confirm Password:"));
        JPasswordField txtRegConfirm = new JPasswordField();
        registerDialog.add(txtRegConfirm);

        JButton btnRegSubmit = new JButton("Register");
        btnRegSubmit.addActionListener(e -> {
            String username = txtRegUsername.getText();
            String password = new String(txtRegPassword.getPassword());
            String confirm = new String(txtRegConfirm.getPassword());
            if (password.equals(confirm) && !username.isEmpty()) {
                String id = "U" + (1000 + model.getUsers().size());
                User newUser = new User(id, username, model.hashPassword(password), role); // Need to make hashPassword public or handle
                model.addUser(newUser);
                JOptionPane.showMessageDialog(registerDialog, "Registration successful");
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, "Passwords do not match or invalid input");
            }
        });
        registerDialog.add(btnRegSubmit);

        JButton btnRegCancel = new JButton("Cancel");
        btnRegCancel.addActionListener(e -> registerDialog.dispose());
        registerDialog.add(btnRegCancel);

        registerDialog.setVisible(true);
    }
}