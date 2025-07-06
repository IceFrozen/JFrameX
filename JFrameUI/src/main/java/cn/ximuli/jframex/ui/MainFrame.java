package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.ui.component.DesktopPanel;
import cn.ximuli.jframex.ui.component.StatePanel;
import cn.ximuli.jframex.ui.menu.MenuBar;
import cn.ximuli.jframex.ui.menu.ToolBar;
import cn.ximuli.jframex.ui.menu.ToolBar2;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SystemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static java.awt.BorderLayout.*;

import java.awt.*;
import java.io.Serial;
import javax.swing.*;

@Component
public class MainFrame extends JFrame {
    private static final double SCREEN_RATIO_WIDTH = 0.75;  // 宽度占屏幕75%
    private static final double SCREEN_RATIO_HEIGHT = 0.90; // 高度占屏幕65%
    @Serial
    private static final long serialVersionUID = 1L;

    private final JPanel frameContentPane;


    private final ToolBar toolBar;
    private final JPanel toolBar2;

    private final DesktopPanel desktopPanel;

    private final StatePanel statePanel;

    private final MenuBar menuBar;

    @Autowired
    public MainFrame(DesktopPanel desktopPanel, ToolBar toolBar, ToolBar2 toolBar2, StatePanel statePanel, MenuBar menuBar) throws HeadlessException {
        this.desktopPanel = desktopPanel;
        this.toolBar = toolBar;
        this.toolBar2 = toolBar2;
        this.statePanel = statePanel;
        this.menuBar = menuBar;

        setTitle(I18nHelper.getMessage("app.mainframe.title"));
        desktopPanel.setSize(getScreenRatioSize());
        setJMenuBar(menuBar);
        setSize(getScreenRatioSize());
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameContentPane = new JPanel();
        frameContentPane.setLayout(new BorderLayout());
        frameContentPane.add(toolBar2, PAGE_START);
        //frameContentPane.add(toolBar, NORTH);
        frameContentPane.add(desktopPanel, CENTER);
        frameContentPane.add(statePanel, SOUTH);
        this.setContentPane(frameContentPane);
        platformInit();
    }

    private void platformInit() {
        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            JRootPane rootPane = getRootPane();
            if (SystemInfo.isMacFullWindowContentSupported) {
                rootPane.putClientProperty("apple.awt.fullWindowContent", ConvertUtil.toBool(System.getProperty("apple.awt.fullWindowContent", "false")));
                rootPane.putClientProperty("apple.awt.transparentTitleBar",     ConvertUtil.toBool(System.getProperty("apple.awt.transparentTitleBar", "false")));
                rootPane.putClientProperty(FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE);
                // hide window title
                if (SystemInfo.isJava_17_orLater){
                    rootPane.putClientProperty("apple.awt.windowTitleVisible", ConvertUtil.toBool(System.getProperty("apple.awt.windowTitleVisible", "false")));
                } else {
                    setTitle(null);
                }
            }
            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater) {
                rootPane.putClientProperty("apple.awt.fullscreenable", ConvertUtil.toBool(System.getProperty("apple.awt.fullscreenable", "false")));
            }
        }
    }

    public static Dimension getScreenRatioSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int adaptedWidth = (int)(screenSize.width  * SCREEN_RATIO_WIDTH);
        int adaptedHeight = (int)(screenSize.height  * SCREEN_RATIO_HEIGHT);
        return new Dimension(adaptedWidth, adaptedHeight);
    }

    public StatePanel getStatePanel() {
        return this.statePanel;
    }
}