package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.ui.menu.MenuBar;
import cn.ximuli.jframex.ui.menu.ToolBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.awt.BorderLayout.*;
import static javax.swing.border.EtchedBorder.RAISED;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

@Component
public class MainFrame extends JFrame {
    private static final double SCREEN_RATIO_WIDTH = 0.75;  // 宽度占屏幕75%
    private static final double SCREEN_RATIO_HEIGHT = 0.65; // 高度占屏幕65%
    private static final long serialVersionUID = 1L;

    JPanel frameContentPane;

    ToolBar toolBar;

    DesktopPanel desktopPanel;

    StatePanel statePanel;

    MenuBar menuBar;

    @Autowired
    public MainFrame(DesktopPanel desktopPanel, ToolBar toolBar, StatePanel statePanel, MenuBar menuBar) throws HeadlessException {
        this.desktopPanel = desktopPanel;
        this.toolBar = toolBar;
        this.statePanel = statePanel;
        this.menuBar = menuBar;

        setTitle(I18nHelper.getMessage("app.mainframe.title"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int adaptedWidth = (int)(screenSize.width  * SCREEN_RATIO_WIDTH);
        int adaptedHeight = (int)(screenSize.height  * SCREEN_RATIO_HEIGHT);
        setJMenuBar(menuBar);
        setSize(adaptedWidth, adaptedHeight);
        setLocationRelativeTo(null); // 屏幕居中
        setMinimumSize(new Dimension(800, 600)); // 最小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameContentPane = new JPanel();
        frameContentPane.setLayout(new BorderLayout());
        frameContentPane.add(desktopPanel, CENTER);
        frameContentPane.add(toolBar,NORTH);
        frameContentPane.add(statePanel, SOUTH);
        this.setContentPane(frameContentPane);
    }
}