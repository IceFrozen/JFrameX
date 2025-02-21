package cn.ximuli.jframex.ui.test;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JMemuWithToolBarExample {
    public static void main(String[] args) {
        // 创建 JFrame 实例
        JFrame frame = new JFrame("JMemu 示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        //frame.setJMenuBar(menuBar);

        // 创建工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().setSize(new Dimension(500, 500));
        frame.getContentPane().add(toolBar,  BorderLayout.NORTH);

        // 创建“文件”菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        // 创建“打开”功能
        JMenuItem openMenuItem = new JMenuItem("打开");
        fileMenu.add(openMenuItem);

        JButton openBtn = new JButton("打开");
        openBtn.setIcon(new  ImageIcon("open-icon.png"));
        openBtn.setToolTipText(" 打开文件");
        openBtn.setSize(100, 100);

        openMenuItem.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(" 打开文件");
                // 在此处添加实际的打开逻辑
            }
        });

        openBtn.setAction(openMenuItem.getAction());
        toolBar.add(openBtn);

        // 创建“保存”功能
        JMenuItem saveMenuItem = new JMenuItem("保存");
        fileMenu.add(saveMenuItem);

        JButton saveBtn = new JButton("保存");
        saveBtn.setIcon(new  ImageIcon("save-icon.png"));
        saveBtn.setToolTipText(" 保存文件");

        saveMenuItem.addActionListener(new  ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(" 保存文件");
                // 在此处添加实际的保存逻辑
            }
        });

        saveBtn.setAction(saveMenuItem.getAction());
        toolBar.add(saveBtn);

        // 自定义按钮样式
        openBtn.setBackground(Color.BLACK);
        saveBtn.setBackground(Color.BLACK);

        openBtn.addMouseListener(new  MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                openBtn.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                openBtn.setBackground(Color.WHITE);
            }
        });

        saveBtn.addMouseListener(new  MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                saveBtn.setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                saveBtn.setBackground(Color.WHITE);
            }
        });

        // 设置窗口大小并显示
        frame.setSize(400,  300);
        frame.setVisible(true);
    }
}