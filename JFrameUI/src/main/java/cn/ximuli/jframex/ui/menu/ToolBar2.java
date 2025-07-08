package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.ui.component.SettingInternalJFrame;
import cn.ximuli.jframex.ui.event.CreateFrameEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatDesktop;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.LoggingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

@Component
public class ToolBar2 extends JPanel {
    @Value("${jframex.menu.sync.tool.bar.type: simple}")
    private String menuSyncToolBarTypeSimple;



    private MenuBar menuBar;
    private final JToolBar toolBar = new JToolBar();
    JPanel macFullWindowContentButtonsPlaceholder = new JPanel();
    Color accentColor;
    JLabel accentColorLabel;

    private static final String[] accentColorKeys = {
            "Jframex.accent.default", "Jframex.accent.blue", "Jframex.accent.purple", "Jframex.accent.red",
            "Jframex.accent.orange", "Jframex.accent.yellow", "Jframex.accent.green",
    };
    private static final String[] accentColorNames = {
            "Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",
    };
    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];

    private ToolBar2() {
    }

    @Autowired
    public ToolBar2(MenuBar frameMenuBar) {
        super();
        this.menuBar = frameMenuBar;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        //======== macFullWindowContentButtonsPlaceholder ========
        macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        add(macFullWindowContentButtonsPlaceholder, BorderLayout.WEST);
        //======== toolBar ========
        toolBar.setMargin(new Insets(1, 3, 1, 3));
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

        add(toolBar, BorderLayout.CENTER);
        // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
        macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");
        // uncomment this line to see title bar buttons placeholders in fullWindowContent mode
        //UIManager.put("FlatLaf.debug.panel.showPlaceholders", true);
        initAccentColors();
        FlatDesktop.setPreferencesHandler(() -> {

            FrameManager.publishEvent(new CreateFrameEvent<>(SettingInternalJFrame.class));


        });
    }

    private void initAccentColors() {
        accentColorLabel = new JLabel("Accent color: ");
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(accentColorLabel);

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
        for (int i = 0; i < accentColorButtons.length; i++)
            accentColorButtons[i].setVisible(isAccentColorSupported);
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
}
