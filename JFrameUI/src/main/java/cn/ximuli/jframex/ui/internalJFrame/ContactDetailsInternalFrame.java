package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.event.UserLogoutEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.manager.UICreator;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


@Mate(value = "app.contact.info", icon = "icons/tab_component", order = 1, id = "app.contact.info")
@Slf4j
public class ContactDetailsInternalFrame extends CommonInternalJFrame {
    private final ResourceLoaderManager resources;
    private JPanel mainPanel;
    private JPanel widgetsPanel;


    public ContactDetailsInternalFrame(ResourceLoaderManager resources, DesktopPanel desktopPanel) {
        super(resources, desktopPanel);
        this.resources = resources;
        setTitle(I18nHelper.getMessage(getClass().getAnnotation(Mate.class).value()));
        setFrameIcon(resources.getIcon(getClass().getAnnotation(Mate.class).icon()));
        // Make the frame non-draggable and non-resizable
        setMaximizable(false);
        setResizable(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }

    private JPanel createContactPanel() {
        LoggedInUser currentUser = FrameManager.getCurrentUser();
        User user = currentUser.getUser();

        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 30 20 20 20", "[grow, center]", ""));
        panel.setBackground(Color.WHITE);

        // Avatar
        JLabel avatarLabel = new JLabel();
        ImageIcon avatarIcon = new ImageIcon(resources.getImage("avatar").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        avatarLabel.setIcon(avatarIcon);
        panel.add(avatarLabel);

        // Name
        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(nameLabel);

        // Title
        JLabel titleLabel = new JLabel(user.getDepartment().getName());
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(titleLabel);

        // Phone
        JLabel ratingLabel = new JLabel(user.getPhone());
        ratingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(ratingLabel);

        // ===== Contact Info Panel =====
        JPanel contactInfoPanel = new JPanel(new MigLayout("wrap 2, insets 10, gapx 5", "[][grow]", ""));
        contactInfoPanel.setBackground(new Color(250, 250, 250));
        contactInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        contactInfoPanel.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        contactInfoPanel.add(new JLabel("Email:"), "align left");
        contactInfoPanel.add(new JLabel(user.getEmail()), "wrap");
        contactInfoPanel.add(new JLabel("Phone:"), "align left");
        contactInfoPanel.add(new JLabel(user.getPhone()), "wrap");

        panel.add(contactInfoPanel, "growx, wrap");

        // ===== Tags Panel =====
        JPanel tagsPanel = new JPanel(new MigLayout("insets 10, wrap 4, gapx 8, gapy 5", "[]", ""));
        tagsPanel.setBackground(new Color(232, 246, 249));
        tagsPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 230, 240)));
        tagsPanel.putClientProperty(FlatClientProperties.STYLE, "arc:12");

        String[] tags = {"taggggg 1", "taggggg 2", "taggggg 3", "taggggg 4", "taggggg 5", "taggggg 6", "taggggg 7"};
        for (String tag : tags) {
            JLabel tagLabel = new JLabel(tag);
            tagLabel.setOpaque(true);
            tagLabel.putClientProperty(FlatClientProperties.STYLE, "arc:999; border: 2,10,2,10");
            tagLabel.setBackground(new Color(0xb8e4f3));
            tagLabel.setForeground(new Color(0x135b76));
            tagsPanel.add(tagLabel);
        }

        panel.add(tagsPanel, "growx, wrap");

        // ===== Owner =====
        JLabel ownerLabel = new JLabel("Owner: Mark Hansen");
        ownerLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(ownerLabel);

        return panel;
    }


    private JPanel createWidgetsPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 10", "[grow]", ""));
        panel.setBackground(new Color(240, 240, 240));

        Font font = UIManager.getFont("defaultFont");
        // Login and Cancel Buttons
        JButton logoutButton = new JButton(I18nHelper.getMessage("app.logout.button"));
        logoutButton.setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2)); // Increased font size
        logoutButton.setBackground(new Color(57, 141, 215));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.putClientProperty("JButton.buttonType", "roundRect"); // Round corners
        logoutButton.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        logoutButton.putClientProperty("JButton.arc", 10); // Arc for rounding
        logoutButton.setPreferredSize(new Dimension(150, 40)); // Increased button size
        logoutButton.addActionListener(e -> handleLogOut());

        panel.add(logoutButton, "grow, push");

        return panel;
    }

    private void handleLogOut() {
        int confirm = JOptionPane.showConfirmDialog(this,
                I18nHelper.getMessage("app.logout.confirm.message"),
                I18nHelper.getMessage("app.logout.confirm.title"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        FrameManager.publishEvent(new UserLogoutEvent(this));
    }

    public void refreshUI() {
        setLayout(new BorderLayout());
        // Main panel with MigLayout
        this.mainPanel = createContactPanel();
        // Bottom Widgets Panel
        this.widgetsPanel = createWidgetsPanel();
        add(mainPanel, BorderLayout.CENTER);
        add(widgetsPanel, BorderLayout.SOUTH);
    }

    @Override
    public void showHint(boolean b) {

    }

    private void adjustFrameSize() {
        // Get the desktop pane size
        Dimension desktopSize = desktopPane.getSize();

        // Use 1/3 of desktop height as the base height
        int baseHeight = (int) (desktopSize.height / 1.7);

        // Maintain 2:1 width-to-height ratio
        int width = (int) (baseHeight * 0.6);
        int height = baseHeight;

        // Ensure the size doesn't exceed the desktop pane's dimensions
        width = Math.min(width, desktopSize.width - 20); // Add padding
        height = Math.min(height, desktopSize.height - 20);

        // Set the frame size
        setSize(width, height);

        // Center the frame within the desktop pane
        int x = (desktopSize.width - width) / 2;
        int y = (desktopSize.height - height) / 2;
        setLocation(x, y);
    }
}