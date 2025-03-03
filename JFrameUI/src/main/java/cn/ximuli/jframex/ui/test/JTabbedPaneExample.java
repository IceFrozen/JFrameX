package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JTabbedPaneExample {
    public static void main(String[] args) throws IOException {
        // 创建主窗口
        JFrame frame = new JFrame("JTabbedPane 示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        // 创建 JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // 创建第一个标签页：JSlider 和 JSpinner 示例
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        // JSlider 示例
        JLabel sliderLabel = new JLabel("JSlider:");
        JSlider slider = new JSlider(0, 100, 50);  // 最小值0，最大值100，初始值50
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        panel1.add(sliderLabel);
        panel1.add(slider);

        // JSpinner 示例
        JLabel spinnerLabel = new JLabel("JSpinner:");
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));  // 初始值1，最小值1，最大值10，步长1
        panel1.add(spinnerLabel);
        panel1.add(spinner);

        tabbedPane.addTab("标签页 1", panel1); // 添加第一个标签页

        // 创建第二个标签页：JSplitPane 和 JToolTip 示例
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());

        // JSplitPane 示例
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JTextArea("左侧面板内容", 10, 30),
                new JTextArea("右侧面板内容", 10, 30));
        panel2.add(splitPane, BorderLayout.CENTER);

        // JToolTip 示例
        JButton toolTipButton = new JButton("鼠标悬停显示提示");
        toolTipButton.setToolTipText("这是一个JToolTip示例");
        panel2.add(toolTipButton, BorderLayout.SOUTH);

        tabbedPane.addTab("标签页 2", panel2); // 添加第二个标签页

        // 创建第三个标签页：JViewport 和 JPopupMenu 示例
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());

        // JViewport 示例
        JTextArea textArea = new JTextArea("这是一个包含 JViewport 的文本区域。\n你可以在这里输入一些文本。", 10, 20);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(200, 100));
        panel3.add(scrollPane, BorderLayout.CENTER);

        // JPopupMenu 示例
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem1 = new JMenuItem("菜单项 1");
        JMenuItem menuItem2 = new JMenuItem("菜单项 2");
        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);

        // 为文本区域添加右键菜单
        textArea.setComponentPopupMenu(popupMenu);

        tabbedPane.addTab("标签页 3", panel3); // 添加第三个标签页

        // 创建第四个标签页：JEditorPane 示例
        JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout());

        // 创建 JEditorPane 并加载 HTML 内容
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);  // 设置为不可编辑
        String htmlContent = "<html><h1>JEditorPane 示例</h1><p>这是一个简单的 HTML 内容</p></html>";
        editorPane.setText(htmlContent);

        // 将 JEditorPane 添加到滚动面板中
        JScrollPane scrollPaneEditor = new JScrollPane(editorPane);
        panel4.add(scrollPaneEditor, BorderLayout.CENTER);

        tabbedPane.addTab("标签页 4", panel4); // 添加第四个标签页

        // 将 JTabbedPane 添加到窗口中
        frame.add(tabbedPane);

        // 显示窗口
        frame.setVisible(true);
    }
}