package cn.ximuli.jframex.ui.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

@Component
public class ToolBar extends JToolBar {
    private MenuBar menuBar;
    /**
     * 默认的构造方法
     */
    private ToolBar() {
    }
    @Autowired
    public ToolBar(MenuBar frameMenuBar) {
        super();
        this.menuBar = frameMenuBar;
        initialize();
    }

    /**
     * 界面初始化方法
     *
     */
    private void initialize() {
        setSize(new Dimension(600, 30));
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFloatable(false);
        for (JMenu jMenu : menuBar.getMenuList()) {
            java.awt.Component[] menuComponents = jMenu.getMenuComponents();
            for (java.awt.Component menuComponent : menuComponents) {
                if (menuComponent instanceof JMenuItem item) {
                    add(createToolButton((item)));
                }
            }
        }
    }

    /**
     * 创建工具栏按钮的方法
     *
     * @return javax.swing.JButton
     */
    private JButton createToolButton(final JMenuItem item) {
        JButton button = new JButton();
        button.setText(item.getText());
        button.setToolTipText(item.getText());
        button.setIcon(item.getIcon());
        button.setFocusable(false);
        button.addActionListener(e -> item.doClick());
        return button;
    }
}
