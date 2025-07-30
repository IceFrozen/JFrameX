package cn.ximuli.jframex.ui.component.menu;


import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.internalJFrame.ContactDetailsInternalFrame;
import cn.ximuli.jframex.ui.internalJFrame.SettingInternalJFrame;
import cn.ximuli.jframex.ui.event.CreateFrameEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

@Slf4j
public class ToolBar extends JPanel {
    private MenuBar menuBar;
    private ResourceLoaderManager resources;
    private final JToolBar toolBar = new JToolBar();
    private JPanel macFullWindowContentButtonsPlaceholder = new JPanel();
    private Color accentColor;
    private JLabel accentColorLabel;

    private static final String[] accentColorKeys = {
            "Jframex.accent.default", "Jframex.accent.blue", "Jframex.accent.purple", "Jframex.accent.red",
            "Jframex.accent.orange", "Jframex.accent.yellow", "Jframex.accent.green",
    };
    private static final String[] accentColorNames = {
            "Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",
    };
    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];

    private ToolBar() {
    }


    public ToolBar(MenuBar frameMenuBar, ResourceLoaderManager resources) {
        super();
        this.menuBar = frameMenuBar;
        this.resources = resources;
        initialize();
    }

    private void initialize() {
        if (SystemInfo.isMacOS) {
            initializeMac();
        } else if (SystemInfo.isWindows) {
            initializeWins();
        } else {
            initializeLinux();
        }
        //UIManager.put("FlatLaf.debug.panel.showPlaceholders", true);

        for (JMenu jMenu : menuBar.getMenuList()) {
            java.awt.Component[] menuComponents = jMenu.getMenuComponents();
            for (java.awt.Component menuComponent : menuComponents) {
                if (menuComponent instanceof JMenuItem item) {
                    JButton toolButton = createToolButton((item));
                    if (!Objects.isNull(toolButton)) {
                        toolBar.add(createToolButton((item)));
                    }
                }
            }
        }

        addSettingButton();
        initAccentColors();
        createUserButton();
        FlatDesktop.setPreferencesHandler(() -> FrameManager.publishEvent(new CreateFrameEvent<>(SettingInternalJFrame.class)));
    }

    private void initAccentColors() {
        log.info("initAccentColors");
        accentColorLabel = new JLabel("Accent color: ");
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(accentColorLabel, BorderLayout.WEST);

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i] = new JToggleButton(new AccentColorIcon(accentColorKeys[i]));
            accentColorButtons[i].setToolTipText(accentColorNames[i]);
            accentColorButtons[i].addActionListener(this::accentColorChanged);
            toolBar.add(accentColorButtons[i]);
            group.add(accentColorButtons[i]);
        }

        accentColorButtons[0].setSelected(true);

        FlatLaf.setSystemColorGetter(name -> name.equals("accent") ? accentColor : null);

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName()))
                updateAccentColorButtons();
        });

        updateAccentColorButtons();
    }

    private void accentColorChanged(ActionEvent e) {
        String accentColorKey = null;
        for (int i = 0; i < accentColorButtons.length; i++) {
            if (accentColorButtons[i].isSelected()) {
                accentColorKey = accentColorKeys[i];
                break;
            }
        }

        accentColor = (accentColorKey != null && accentColorKey != accentColorKeys[0])
                ? UIManager.getColor(accentColorKey)
                : null;

        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    private void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported =
                lafClass == FlatLightLaf.class ||
                        lafClass == FlatDarkLaf.class ||
                        lafClass == FlatIntelliJLaf.class ||
                        lafClass == FlatDarculaLaf.class ||
                        lafClass == FlatMacLightLaf.class ||
                        lafClass == FlatMacDarkLaf.class;

        accentColorLabel.setVisible(isAccentColorSupported);
        for (JToggleButton accentColorButton : accentColorButtons) {
            accentColorButton.setVisible(isAccentColorSupported);
        }
    }

    private static class AccentColorIcon extends FlatAbstractIcon {
        private final String colorKey;

        AccentColorIcon(String colorKey) {
            super(16, 16, null);
            this.colorKey = colorKey;
        }

        @Override
        protected void paintIcon(java.awt.Component c, Graphics2D g) {
            Color color = UIManager.getColor(colorKey);
            if (color == null)
                color = Color.lightGray;
            else if (!c.isEnabled()) {
                color = FlatLaf.isLafDark()
                        ? ColorFunctions.shade(color, 0.5f)
                        : ColorFunctions.tint(color, 0.6f);
            }

            g.setColor(color);
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
        }
    }

    private JButton createToolButton(final JMenuItem item) {
        JButton button = null;
        Object syncType = item.getClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY);
        if (MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE.equals(syncType)) {
            button = new JButton();
            button.setToolTipText(item.getToolTipText());
            button.setIcon(item.getIcon());
            ActionListener[] actionListeners = item.getActionListeners();
            button.setFocusable(false);
            for (ActionListener actionListener : actionListeners) {
                button.addActionListener(actionListener);
            }
        }
        return button;
    }


    public void addSettingButton() {
        JButton button = new JButton();
        button.setIcon(resources.getIcon("icons/settings"));
        button.addActionListener(e -> FrameManager.publishEvent(new CreateFrameEvent<>(SettingInternalJFrame.class)));
        toolBar.add(button);
    }

    public void initializeMac() {
        setLayout(new BorderLayout());
        //======== macFullWindowContentButtonsPlaceholder ========
        macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        boolean fullScreenableBar = ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_LAF_USE_SCREEN_MENUBAR));
        if (fullScreenableBar) {
            add(macFullWindowContentButtonsPlaceholder, BorderLayout.WEST);
            //======== toolBar ========
            toolBar.setMargin(new Insets(1, 3, 1, 3));
            add(toolBar, BorderLayout.CENTER);
            // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
            macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");

        } else{
            add(toolBar, BorderLayout.CENTER);
            // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
            macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");
        }


    }

    public void initializeLinux() {

    }

    public void initializeWins() {

    }


    public void createUserButton() {
        // add "Users" button to menubar
        boolean fullScreenableBar = ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_LAF_USE_SCREEN_MENUBAR));

        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(resources.getIcon("icons/users"));
        usersButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e ->   FrameManager.publishEvent(new CreateFrameEvent<>(ContactDetailsInternalFrame.class)));
        if (fullScreenableBar) {
            toolBar.add(usersButton, BorderLayout.LINE_END);
        } else {
            menuBar.add(Box.createGlue());
            menuBar.add(usersButton);
        }
    }
}
