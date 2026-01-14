package view;

import model.UserRole;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView(UserRole role) {
        setTitle(role + " Login");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(role + " Login", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("Login");

        panel.add(lblTitle);
        panel.add(txtUsername);
        panel.add(txtPassword);
        panel.add(btnLogin);

        add(panel);
    }

    public String getUsername() {
        return txtUsername.getText();
    }

    public char[] getPassword() {
        return txtPassword.getPassword();
    }

    public void setLoginListener(Runnable action) {
        btnLogin.addActionListener(e -> action.run());
    }
}
