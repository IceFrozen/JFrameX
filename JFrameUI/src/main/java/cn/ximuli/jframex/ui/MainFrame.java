package cn.ximuli.jframex.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.awt.BorderLayout.*;
import java.awt.*;
import javax.swing.*;
@Component
public class MainFrame extends JFrame {
    private static final double SCREEN_RATIO_WIDTH = 0.75;  // 宽度占屏幕75%
    private static final double SCREEN_RATIO_HEIGHT = 0.65; // 高度占屏幕65%
    private static final long serialVersionUID = 1L;
    private JPanel frameContentPane = null;

    private DesktopPanel desktopPanel;

    @Autowired
    public MainFrame(DesktopPanel desktopPanel) throws HeadlessException {
        this.desktopPanel = desktopPanel;
        setTitle(I18nHelper.getMessage("app.mainframe.title"));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int adaptedWidth = (int)(screenSize.width  * SCREEN_RATIO_WIDTH);
        int adaptedHeight = (int)(screenSize.height  * SCREEN_RATIO_HEIGHT);

        setSize(adaptedWidth, adaptedHeight);
        setLocationRelativeTo(null); // 屏幕居中
        setMinimumSize(new Dimension(800, 600)); // 最小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        frameContentPane = new JPanel();
        frameContentPane.setLayout(new BorderLayout());
        frameContentPane.add(desktopPanel, CENTER);
        this.setContentPane(frameContentPane);
    }
}