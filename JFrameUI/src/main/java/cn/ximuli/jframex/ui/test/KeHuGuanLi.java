package cn.ximuli.jframex.ui.test;

import org.springframework.stereotype.Component;

import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
@Component
public class KeHuGuanLi extends JInternalFrame {
    public KeHuGuanLi() {
        setIconifiable(true);
        setClosable(true);
        setTitle("客户信息管理");
        JTabbedPane tabPane = new JTabbedPane();
        final KeHuXiuGaiPanel khxgPanel = new KeHuXiuGaiPanel();
        final KeHuTianJiaPanel khtjPanel = new KeHuTianJiaPanel();
        tabPane.addTab("客户信息添加", null, khtjPanel, "客户添加");
        tabPane.addTab("客户信息修改与删除", null, khxgPanel, "修改与删除");
        getContentPane().add(tabPane);
        tabPane.addChangeListener(e -> khxgPanel.initComboBox());
        pack();
    }
}
