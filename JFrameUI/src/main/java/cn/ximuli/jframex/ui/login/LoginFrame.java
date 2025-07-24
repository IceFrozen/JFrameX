package cn.ximuli.jframex.ui.login;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.service.LoginService;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.event.UserLoginEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

@Component
@Slf4j
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckbox;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel forgetPasswordLink;
    private ResourceLoaderManager resources;

    private LoginService loginService;

    public LoginFrame(ResourceLoaderManager resources, LoginService loginService) {
        this.resources = resources;
        this.setVisible(false);
        this.loginService = loginService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(I18nHelper.getMessage("app.login.title"));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); // Remove title bar and borders
        Dimension windowSize = MainFrame.getScreenRatioSize();
        int windowHeight = Math.max(windowSize.height / 3 , 300) ; // Reserve space for progress bar
        int windowWidth = windowHeight * 2;
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);

        // Add escape key to close the frame
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Main panel with split layout
        JPanel mainPanel = new JPanel(new MigLayout("fill, insets 0", "[100%][100%]", "[100%]"));
        mainPanel.setBackground(new Color(57, 141, 215)); // Blue background

        // Left panel (illustration)
        JPanel illustrationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Image backImage = resources.getImage("loginPanel");
                // Draw background image without stretching (centered)
                int width = getWidth();
                int height = getHeight();

                int imgWidth = backImage.getWidth(this);
                int imgHeight = backImage.getHeight(this);

                // Calculate scaling factor to cover the entire panel
                double scaleX = (double) width / imgWidth ;
                double scaleY = (double) height / imgHeight ;
                double scale = Math.max(scaleX, scaleY); // Use larger scale to cover panel

                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);

                // Center the image
                int x = (width - scaledWidth) / 2;
                int y = (height - scaledHeight) / 2;
                g2d.drawImage(backImage, x, y, scaledWidth, scaledHeight, this);
                g2d.dispose();
            }
        };

        illustrationPanel.setBackground(new Color(57, 141, 215)); // Blue background
        mainPanel.add(illustrationPanel, "grow");

        // Right panel (login form)
        int insetValue = (int) (windowHeight * 0.05); // 5% of window height as inset
        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx, insets " + insetValue, "[right][grow]", "3%[]3%[]3%[]3%[]3%[]")); // Dynamic row heights
        //JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[right][grow]", "[][][]10[][][]")); // Adjusted for new row
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel(I18nHelper.getMessage("app.login.title"));
        Font font = UIManager.getFont("defaultFont");

        titleLabel.setFont(new Font(font.getName(), Font.BOLD,  font.getSize()  * 2));
        formPanel.add(titleLabel, "span 2, align center");
        JLabel subtitleLabel = new JLabel(I18nHelper.getMessage("app.login.subtitle"));
        subtitleLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 5));
        formPanel.add(subtitleLabel, "span 2, align center");

        // Username
        JLabel usernameLabel = new JLabel(I18nHelper.getMessage("app.login.label.username"));
        usernameLabel.setFont(new Font(font.getName(), Font.PLAIN, font.getSize() ));
        usernameField = new JTextField(20);
        usernameField.setPreferredSize(new Dimension(usernameField.getPreferredSize().width, usernameField.getPreferredSize().height + 15)); // Wider inp
        usernameField.putClientProperty("JTextField.placeholderText", I18nHelper.getMessage("app.login.username.placeholder"));
        usernameField.putClientProperty("JTextField.leadingIcon", resources.getIcon("icons/user"));
        formPanel.add(usernameField, "span 2, growx");

        // Password
        JLabel passwordLabel = new JLabel(I18nHelper.getMessage("app.login.password"));
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, font.getSize() ));
        passwordField = new JPasswordField(20);
        passwordField.putClientProperty("JTextField.placeholderText", I18nHelper.getMessage("app.login.password.placeholder"));
        passwordField.setPreferredSize(new Dimension(passwordField.getPreferredSize().width, passwordField.getPreferredSize().height + 15)); // Wider input field
        passwordField.putClientProperty("JTextField.leadingIcon", resources.getIcon("icons/locked")); // Corrected to lock icon
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        formPanel.add(passwordField, "span 2 , growx");

        // Remember and Forget Password in the same row, centered
        rememberCheckbox = new JCheckBox(I18nHelper.getMessage("app.login.remember"));
        forgetPasswordLink = new JLabel("<html><a href='#'>" + I18nHelper.getMessage("app.login.forget") + "</a></html>");
        forgetPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgetPasswordLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginFrame.this, I18nHelper.getMessage("app.login.forget.message"));
            }
        });
        formPanel.add(rememberCheckbox, "align center");
        formPanel.add(forgetPasswordLink, "align right"); // Same row, reduced gap

        // Login and Cancel Buttons
        loginButton = new JButton(I18nHelper.getMessage("app.login.button"));
        loginButton.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2)); // Increased font size
        loginButton.setBackground(new Color(57, 141, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.putClientProperty("JButton.buttonType", "roundRect"); // Round corners
        loginButton.putClientProperty("JButton.arc", 10); // Arc for rounding
        loginButton.setPreferredSize(new Dimension(150, 40)); // Increased button size
        loginButton.addActionListener(e -> handleLogin());

        cancelButton = new JButton(I18nHelper.getMessage("app.login.cancel"));
        cancelButton.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
        cancelButton.setBackground(new Color(57, 141, 215)); // Gray background for cancel
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.putClientProperty("JButton.buttonType", "roundRect");
        cancelButton.putClientProperty("JButton.arc", 10);

        cancelButton.setPreferredSize(new Dimension(150, 40));
        cancelButton.addActionListener(e -> System.exit(0)); // Close the frame on cancel
        JPanel buttonPanel = new JPanel(new MigLayout("fill, insets 0", "[]", "[]"));
        buttonPanel.add(loginButton, "growx");
        buttonPanel.add(cancelButton, "growx");
        formPanel.add(buttonPanel, "span 2, align center, gapy 10");

        mainPanel.add(formPanel, "grow, align center"); // Center the form panel
        add(mainPanel);


        // Add enter key to trigger login
        getRootPane().registerKeyboardAction(
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loginButton.doClick();
                    }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, I18nHelper.getMessage("app.login.message.empty"));
            return;
        }

        LoggedInUser login = loginService.login(username, password);
        if (login == null) {
            JOptionPane.showMessageDialog(this, I18nHelper.getMessage("app.message.login.invalidate")
                    , I18nHelper.getMessage("app.message.title.error"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        FrameManager.publishEvent(new UserLoginEvent(login));
    }


}