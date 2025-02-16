package cn.ximuli.jframex.ui.login;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@Component
public class LoginDialog extends JFrame {

    private int width = 295 * 2;
    private int height = 188 * 2;
    private static final long serialVersionUID = 1L;
    private LoginPanel loginPanel = null;
    private JLabel userNameLabel = null;
    private JTextField userField = null;
    private JLabel passwordLabel = null;
    private JPasswordField passwordField = null;
    private JButton loginButton = null;
    private JButton exitButton = null;
    @Autowired
    private ResourceLoaderManager resourceLoaderManager;

    public void initialize() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2 - width / 2, (screen.height - getHeight()) / 2 - height / 2);
        setSize(width, height);
        setTitle(I18nHelper.getMessage("app.login.title"));
        setContentPane(getLoginPanel());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private LoginPanel getLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new LoginPanel();
            loginPanel.setLayout(null);
            loginPanel.setBackground(new Color(0xD8DDC7));

            passwordLabel = new JLabel();
            passwordLabel.setBounds(new Rectangle(85*2, 155, 100, 36));
            passwordLabel.setText(I18nHelper.getMessage("app.login.label.password"));
            loginPanel.add(passwordLabel, null);
            loginPanel.add(getUserField(), null);

            userNameLabel = new JLabel();
            userNameLabel.setText(I18nHelper.getMessage("app.login.label.username"));
            userNameLabel.setBounds(new Rectangle(85*2, 113, 100, 36));
            loginPanel.add(userNameLabel, null);
            loginPanel.add(getPasswordField(), null);



            loginPanel.add(getLoginButton(), null);
            loginPanel.add(getExitButton(), null);
        }
        return loginPanel;
    }

    private JTextField getUserField() {
        if (userField == null) {
            userField = new JTextField();
            userField.setBounds(new Rectangle(130* 2 , 118, 167, 30));
        }
        return userField;
    }

    private JPasswordField getPasswordField() {
        if (passwordField == null) {
            passwordField = new JPasswordField();
            passwordField.setBounds(new Rectangle(130*2, 158, 167, 30));
            passwordField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == '\n')
                        loginButton.doClick();
                }
            });
        }
        return passwordField;
    }

    private JButton getLoginButton() {
        if (loginButton == null) {
            loginButton = new JButton();
            loginButton.setBounds(new Rectangle(100*2, 230, 48, 20));
            loginButton.setIcon(resourceLoaderManager.getIcon("loginButton"));
            loginButton.addActionListener(e -> {
                String username = userField.getText();
                String passStr = new String(passwordField.getPassword());
//                    if (!Dao.checkLogin(userStr, passStr)) {
//                        JOptionPane.showMessageDialog(LoginDialog.this,
//                                "password error", "info",
//                                JOptionPane.ERROR_MESSAGE);
//                        return;
//
//                    }
            });
        }
        return loginButton;
    }

    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setBounds(new Rectangle(170*2, 230, 48, 20));
            exitButton.setIcon(resourceLoaderManager.getIcon("exitButton"));
            exitButton.addActionListener(e -> System.exit(0));
        }
        return exitButton;
    }

}