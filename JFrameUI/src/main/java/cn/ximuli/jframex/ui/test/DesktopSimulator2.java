package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DesktopSimulator2 extends JFrame {
    private JPanel desktop;
    private List<DesktopItem> items;
    private JPopupMenu popupMenu;
    private DesktopItem draggedItem;
    private Point dragOffset;

    public DesktopSimulator2() {
        setTitle("模拟 Windows 桌面");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        items = new ArrayList<>();

        // 创建桌面面板
        desktop = new JPanel(null);
        desktop.setBackground(new Color(0, 120, 215)); // Windows 桌面蓝色
        setContentPane(desktop);

        // 初始化右键菜单
        initPopupMenu();

        // 桌面鼠标监听
        desktop.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });

        // 添加初始文件夹
        addItem(new DesktopItem("我的文件夹", true, 50, 50));
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem newFile = new JMenuItem("新建文本文件");
        JMenuItem newFolder = new JMenuItem("新建文件夹");
        JMenuItem alignItems = new JMenuItem("整理图标");

        newFile.addActionListener(e -> createNewFile());
        newFolder.addActionListener(e -> createNewFolder());
        alignItems.addActionListener(e -> alignItems());

        popupMenu.add(newFile);
        popupMenu.add(newFolder);
        popupMenu.addSeparator();
        popupMenu.add(alignItems);
    }

    private void showPopup(MouseEvent e) {
        popupMenu.show(desktop, e.getX(), e.getY());
    }

    private void addItem(DesktopItem item) {
        items.add(item);
        desktop.add(item);
        desktop.revalidate();
        desktop.repaint();
    }

    private void createNewFile() {
        String name = JOptionPane.showInputDialog(this, "请输入文件名：", "新建文本文件", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            DesktopItem file = new DesktopItem(name + ".txt", false, 50, 50);
            addItem(file);
        }
    }

    private void createNewFolder() {
        String name = JOptionPane.showInputDialog(this, "请输入文件夹名：", "新建文件夹", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            DesktopItem folder = new DesktopItem(name, true, 50, 50);
            addItem(folder);
        }
    }

    private void alignItems() {
        int x = 10, y = 10;
        int maxHeight = 0;
        for (DesktopItem item : items) {
            if (x + 100 > desktop.getWidth()) {
                x = 10;
                y += maxHeight + 10;
                maxHeight = 0;
            }
            item.setLocation(x, y);
            maxHeight = Math.max(maxHeight, item.getHeight());
            x += 100;
        }
        desktop.repaint();
    }

    class DesktopItem extends JPanel {
        private JLabel icon;
        private JLabel nameLabel;
        private boolean isFolder;

        public DesktopItem(String name, boolean isFolder, int x, int y) {
            this.isFolder = isFolder;
            setLayout(new BorderLayout());
            setSize(80, 100);
            setLocation(x, y);
            setOpaque(false);

            // 图标
            icon = new JLabel(isFolder ? getFolderIcon() : getFileIcon());
            icon.setHorizontalAlignment(JLabel.CENTER);

            // 文件名
            nameLabel = new JLabel(name);
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setForeground(Color.WHITE);

            add(icon, BorderLayout.CENTER);
            add(nameLabel, BorderLayout.SOUTH);

            // 拖拽功能
            MouseAdapter dragHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    draggedItem = DesktopItem.this;
                    dragOffset = new Point(e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    draggedItem = null;
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedItem != null) {
                        Point p = SwingUtilities.convertPoint(DesktopItem.this, e.getPoint(), desktop);
                        int newX = p.x - dragOffset.x;
                        int newY = p.y - dragOffset.y;
                        draggedItem.setLocation(newX, newY);
                        desktop.repaint();
                    }
                }
            };

            addMouseListener(dragHandler);
            addMouseMotionListener(dragHandler);

            // 双击打开
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (isFolder) {
                            showFolderContents(name);
                        } else {
                            showFileEditor(name);
                        }
                    }
                }
            });
        }

        private Icon getFolderIcon() {
            return UIManager.getIcon("FileView.directoryIcon");
        }

        private Icon getFileIcon() {
            return UIManager.getIcon("FileView.fileIcon");
        }

        private void showFolderContents(String folderName) {
            JDialog dialog = new JDialog(DesktopSimulator2.this, "文件夹: " + folderName, true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(DesktopSimulator2.this);
            dialog.setLayout(new BorderLayout());

            JTextArea contents = new JTextArea("文件夹内容示例\n文件1.txt\n文件2.txt");
            dialog.add(new JScrollPane(contents), BorderLayout.CENTER);
            dialog.setVisible(true);
        }

        private void showFileEditor(String fileName) {
            JDialog dialog = new JDialog(DesktopSimulator2.this, "编辑: " + fileName, true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(DesktopSimulator2.this);
            dialog.setLayout(new BorderLayout());

            JTextArea editor = new JTextArea("文件内容示例...");
            dialog.add(new JScrollPane(editor), BorderLayout.CENTER);

            JButton save = new JButton("保存");
            save.addActionListener(e -> dialog.dispose());
            dialog.add(save, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DesktopSimulator2 simulator = new DesktopSimulator2();
            simulator.setVisible(true);
        });
    }
}