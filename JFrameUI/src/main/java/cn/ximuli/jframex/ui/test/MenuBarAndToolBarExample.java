package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBarAndToolBarExample {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("JMenuBar 和 JToolBar 示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        // 创建菜单栏 JMenuBar
        JMenuBar menuBar = new JMenuBar();

        // 创建菜单
        JMenu fileMenu = new JMenu("文件");
        JMenu editMenu = new JMenu("编辑");

        // 创建菜单项
        JMenuItem openItem = new JMenuItem("打开");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("退出");

        // 添加菜单项到菜单
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();  // 分隔符
        fileMenu.add(exitItem);

        // 将菜单添加到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // 设置菜单栏
        frame.setJMenuBar(menuBar);

        // 创建工具栏 JToolBar
        JToolBar toolBar = new JToolBar("工具栏");
        JButton openButton = new JButton("打开");
        JButton saveButton = new JButton("保存");
        JButton exitButton = new JButton("退出");

        // 将按钮添加到工具栏
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.addSeparator();  // 分隔符
        toolBar.add(exitButton);

        // 添加工具栏到窗口
        frame.getContentPane().add(toolBar, BorderLayout.NORTH);

        // 给菜单项和按钮添加事件处理
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "打开文件");
            }
        });
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "保存文件");
            }
        });
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 为工具栏按钮添加事件处理
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "打开文件");
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "保存文件");
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 显示窗口
        frame.setVisible(true);
    }
}