package cn.ximuli.jframex.ui.test;

import cn.ximuli.jframex.ui.menu.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JMemuExample {
    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("JMemu 示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        // 创建菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        // 创建 JMenuItem
        JMenuItem openItem = new JMenuItem("打开");
        JMenuItem saveItem = new JMenuItem("保存");
        JMenuItem exitItem = new JMenuItem("退出");

        // 将 JMenuItem 添加到菜单中
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        // 为 JMenuItem 添加事件监听器
        openItem.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(" 打开文件");
            }
        });

        saveItem.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(" 保存文件");
            }
        });

        exitItem.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // 设置窗口大小并显示
        frame.setSize(400,  300);
        frame.setVisible(true);
    }
}