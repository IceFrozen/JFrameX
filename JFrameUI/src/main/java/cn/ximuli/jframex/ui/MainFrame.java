package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.StatePanel;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.menu.ToolBar;
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

        setTitle(I18nHelper.getMessage("app.mainframe.title"));
        setJMenuBar(menuBar);
        setSize(getScreenRatioSize());
        setLocationRelativeTo(null);
        setMinimumSize(MINIMUM_WINDOW_SIZE);

        setContentPane(frameContentPane);
        this.platformInit();
        this.addCloseListener();
    }

    private void platformInit() {
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            JRootPane rootPane = getRootPane();
            System.setProperty(Application.MAC.COM_APPLE_MRJ_APPLICATION_APPLE_MENU_ABOUT_NAME, I18nHelper.getMessage("app.mainframe.title"));
            if (SystemInfo.isMacFullWindowContentSupported) {
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_FULL_WINDOW_CONTENT, ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_FULL_WINDOW_CONTENT, "false")));
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_TRANSPARENT_TITLE_BAR, ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_TRANSPARENT_TITLE_BAR, "false")));
                rootPane.putClientProperty(FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE);
                // hide window title
                if (SystemInfo.isJava_17_orLater) {
                    rootPane.putClientProperty(Application.MAC.APPLE_AWT_WINDOW_TITLE_VISIBLE, ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_WINDOW_TITLE_VISIBLE, "false")));
                } else {
                    setTitle(null);
                }
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) {
                rootPane.putClientProperty(Application.MAC.APPLE_AWT_FULL_FULL_SCREENABLE, ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_AWT_FULL_FULL_SCREENABLE, "false")));
            }
        }
    }

    public static Dimension getScreenRatioSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int adaptedWidth = (int) (screenSize.width * SCREEN_RATIO_WIDTH);
        int adaptedHeight = (int) (screenSize.height * SCREEN_RATIO_HEIGHT);
        return new Dimension(adaptedWidth, adaptedHeight);
    }


    public void addCloseListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(
                        desktopPanel,
                        I18nHelper.getMessage("app.mainframe.close.confirm"),
                        I18nHelper.getMessage("app.mainframe.close.title"),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
}

