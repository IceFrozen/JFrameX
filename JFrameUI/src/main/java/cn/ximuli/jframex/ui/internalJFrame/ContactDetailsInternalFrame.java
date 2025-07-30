package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.event.UserLogoutEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import com.formdev.flatlaf.FlatClientProperties;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Mate(value = "app.menu.view.components.tab", icon = "icons/tab_component", order = 1)
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
        // Add component listener to adjust size when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }


    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 10", "[grow]", ""));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

        // Avatar
        JLabel avatarLabel = new JLabel();
        ImageIcon avatarIcon = new ImageIcon(resources.getImage("avatar").getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        avatarLabel.setIcon(avatarIcon);
        panel.add(avatarLabel, "align center");

        // Name and Title
        JLabel nameLabel = new JLabel("Claudia Mills");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(nameLabel, "align center");

        JLabel titleLabel = new JLabel("Product Manager @ TechCore");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(titleLabel, "align center");

        // Rating
        JLabel ratingLabel = new JLabel("★★★★☆ 128");
        ratingLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(ratingLabel, "align center");

        // Contact Information
        JPanel contactInfoPanel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", ""));
        contactInfoPanel.add(new JLabel(I18nHelper.getMessage("app.contact.info.title")), "wrap");
        contactInfoPanel.add(new JLabel("Email: claudia.mills@techcore.com"));
        contactInfoPanel.add(new JLabel("Phone: +1-415-555-0004"));
        contactInfoPanel.add(new JLabel("Address: West End 123rd St, San Francisco 90210, CA, US"));
        panel.add(contactInfoPanel, "growx");

        // Tags
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] tags = {"tag 1", "tag 2", "tag 3", "tag 4", "tag 5"};
        for (String tag : tags) {
            JLabel tagLabel = new JLabel(tag);
            tagLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            tagLabel.setOpaque(true);
            tagLabel.setBackground(new Color(230, 230, 230));
            tagsPanel.add(tagLabel);
        }
        panel.add(tagsPanel, "growx");

        // Owner
        panel.add(new JLabel("Owner: Mark Hansen"), "wrap");

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
                I18nHelper.getMessage("app.logout.confirm"),
                I18nHelper.getMessage("app.logout.title"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        FrameManager.publishEvent(new UserLogoutEvent( this));
    }

    public void refleshUI() {
        setLayout(new BorderLayout());
        // Main panel with MigLayout
        this.mainPanel = createContactPanel();
        // Bottom Widgets Panel
        this.widgetsPanel = createWidgetsPanel();
        add(mainPanel, BorderLayout.CENTER);
        add(widgetsPanel, BorderLayout.SOUTH);
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