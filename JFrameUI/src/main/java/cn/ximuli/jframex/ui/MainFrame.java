package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.StatePanel;
import cn.ximuli.jframex.ui.internalJFrame.SettingInternalJFrame;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.HintManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.menu.ToolBar;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import static java.awt.BorderLayout.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

@Slf4j
public class MainFrame extends JFrame {
    private static final double SCREEN_RATIO_WIDTH = 0.75;
    private static final double SCREEN_RATIO_HEIGHT = 0.90;
    private static final Dimension MINIMUM_WINDOW_SIZE = new Dimension(800, 600);

    private final JPanel frameContentPane;
    private final ToolBar toolBar;
    private final DesktopPanel desktopPanel;
    private final StatePanel statePanel;
    private final MenuBar menuBar;
    private final ResourceLoaderManager resources;

    public MainFrame(ResourceLoaderManager resources, DesktopPanel desktopPanel, ToolBar toolBar, StatePanel statePanel, MenuBar menuBar) throws HeadlessException {
        this.desktopPanel = desktopPanel;
        this.toolBar = toolBar;
        this.statePanel = statePanel;
        this.menuBar = menuBar;
        this.resources = resources;
        this.frameContentPane = new JPanel();
        this.frameContentPane.setLayout(new BorderLayout());
        this.desktopPanel.setSize(getScreenRatioSize());
        this.frameContentPane.add(toolBar, PAGE_START);
        this.frameContentPane.add(desktopPanel, CENTER);
        this.frameContentPane.add(statePanel, SOUTH);

        setTitle(I18nHelper.getMessage("app.name"));
        setJMenuBar(menuBar);
        setSize(getScreenRatioSize());
        setLocationRelativeTo(null);
        setMinimumSize(MINIMUM_WINDOW_SIZE);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setContentPane(frameContentPane);
        log.debug("MainFrame UI components initialized");
        log.info("MainFrame initialized with size: {}x{}", getWidth(), getHeight());
        this.platformInit();
        this.addCloseListener();
    }

    /**
     * Initializes macOS-specific window properties, such as full-screen support and title bar settings.
     */
    private void platformInit() {
        // macOS-specific settings (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            log.debug("Initializing macOS-specific properties");
            JRootPane rootPane = getRootPane();
            System.setProperty(Application.MAC.COM_APPLE_MRJ_APPLICATION_APPLE_MENU_ABOUT_NAME, I18nHelper.getMessage("app.name"));
            if (SystemInfo.isMacFullWindowContentSupported) {
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_FULL_WINDOW_CONTENT,
                        ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_FULL_WINDOW_CONTENT, "false")));
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_TRANSPARENT_TITLE_BAR,
                        ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_TRANSPARENT_TITLE_BAR, "false")));
                rootPane.putClientProperty(FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING,
                        FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE);
                // Hide window title for macOS
                if (SystemInfo.isJava_17_orLater) {
                    rootPane.putClientProperty(Application.MAC.APPLE_AWT_WINDOW_TITLE_VISIBLE,
                            ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_WINDOW_TITLE_VISIBLE, "false")));
                } else {
                    setTitle(null);
                }
            }

            // Enable full-screen mode for Java 8-10 (not needed for Java 11+)
            if (!SystemInfo.isJava_11_orLater) {
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_FULL_FULL_SCREENABLE,
                        ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_FULL_FULL_SCREENABLE, "false")));
            }
            log.debug("macOS-specific properties configured");
            log.info("macOS-specific initialization completed");
        } else {
            log.debug("Not running on macOS, skipping platform-specific initialization");
        }
    }


    public static Dimension getScreenRatioSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int adaptedWidth = (int) (screenSize.width * SCREEN_RATIO_WIDTH);
        int adaptedHeight = (int) (screenSize.height * SCREEN_RATIO_HEIGHT);
        log.debug("Calculated screen ratio size: {}x{}", adaptedWidth, adaptedHeight);
        return new Dimension(adaptedWidth, adaptedHeight);
    }


    public void addCloseListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                log.debug("Window closing event triggered");
                int result = JOptionPane.showConfirmDialog(
                        MainFrame.this,
                        I18nHelper.getMessage("app.mainframe.close.message"),
                        I18nHelper.getMessage("app.mainframe.close.confirm"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                log.debug("Close dialog result: {}", result == JOptionPane.YES_OPTION ? "YES" : "NO/CANCEL");
                if (result == JOptionPane.YES_OPTION) {
                    log.debug("User confirmed to close the application");
                    log.info("Application closing due to user confirmation");
                    System.exit(0);
                } else {
                    log.debug("User canceled, keeping window open");
                }
            }
        });
    }
}