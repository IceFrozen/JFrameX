package cn.ximuli.jframex.ui.login;

import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.service.LoginService;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.event.UserLoginEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@Component
public class LoginDialog extends JFrame {

    private static final int WIDTH = 295 * 2;
    private static final int HEIGHT = 188 * 2;
    private static final long serialVersionUID = 1L;
    private LoginPanel loginPanel = null;
    private JTextField userField = null;
    private JPasswordField passwordField = null;
    private JButton loginButton = null;
    private JButton exitButton = null;
    @Autowired
    private ResourceLoaderManager resource;
    @Autowired
    private LoginService loginService;

    @Autowired
    private MessageSource messageSource;

    public void initialize() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2 - WIDTH / 2, (screen.height - getHeight()) / 2 - WIDTH / 2);
        setSize(WIDTH, HEIGHT);
        setTitle(I18nHelper.getMessage("app.login.title"));
        setContentPane(getLoginPanel());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private LoginPanel getLoginPanel() {
        if (loginPanel == null) {
            loginPanel = new LoginPanel(resource.getImage("login"));
            loginPanel.setLayout(null);
            loginPanel.setBackground(new Color(0xD8DDC7));

            JLabel passwordLabel = new JLabel();
            passwordLabel.setBounds(new Rectangle(85*2, 155, 100, 36));
            passwordLabel.setText(I18nHelper.getMessage("app.login.label.password"));
            loginPanel.add(passwordLabel, null);
            loginPanel.add(getUserField(), null);

            JLabel userNameLabel = new JLabel();
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
            loginButton.setIcon(resource.getIcon("loginButton"));
            loginButton.addActionListener(e -> {
                String username = userField.getText();
                String passStr = new String(passwordField.getPassword());
                User login = loginService.login(username, passStr);
                if (login == null) {
                    JOptionPane.showMessageDialog(LoginDialog.this, I18nHelper.getMessage("app.message.login.invalidate")
                            , I18nHelper.getMessage("app.message.title.error"),
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                FrameManager.publishEvent(new UserLoginEvent(login));
            });
        }
        return loginButton;
    }

    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton();
            exitButton.setBounds(new Rectangle(170*2, 230, 48, 20));
            exitButton.setIcon(resource.getIcon("exitButton"));
            exitButton.addActionListener(e -> System.exit(0));
        }
        return exitButton;
    }

}