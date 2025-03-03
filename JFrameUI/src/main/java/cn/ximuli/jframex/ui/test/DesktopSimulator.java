package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DesktopSimulator extends JFrame {
    private JPanel desktop;
    private List<DesktopItem> items;
    private JPopupMenu popupMenu;
    private DesktopItem draggedItem;
    private Point dragOffset;

    public DesktopSimulator() {
        setTitle("模拟 Windows 桌面");
        setSize(1000, 700); // 增大窗口尺寸
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
            if (x + 120 > desktop.getWidth()) {
                x = 10;
                y += maxHeight + 10;
                maxHeight = 0;
            }
            item.setLocation(x, y);
            maxHeight = Math.max(maxHeight, item.getHeight());
            x += 120;
        }
        desktop.repaint();
    }

    class DesktopItem extends JPanel {
        private JLabel icon;
        private JLabel nameLabel;
        private boolean isFolder;
        private boolean isHovered = false;

        public DesktopItem(String name, boolean isFolder, int x, int y) {
            this.isFolder = isFolder;
            setLayout(new BorderLayout());
            setSize(100, 130); // 增大图标和整体尺寸
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

            // 鼠标悬停效果
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    setBackground(new Color(255, 255, 255, 50)); // 半透明高亮
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    setBackground(null);
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    draggedItem = DesktopItem.this;
                    dragOffset = new Point(e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    draggedItem = null;
                }
            });

            // 拖拽功能
            addMouseMotionListener(new MouseMotionAdapter() {
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
            });

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (isHovered) {
                g.setColor(new Color(255, 255, 255, 50));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        private Icon getFolderIcon() {
            // 增大文件夹图标
            return UIManager.getIcon("FileView.directoryIcon");

        }

        private Icon getFileIcon() {
            // 增大文件图标
            ImageIcon icon = (ImageIcon) UIManager.getIcon("FileView.fileIcon");
            Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH); // 增大图标
            return new ImageIcon(img);
        }

        private void showFolderContents(String folderName) {
            JDesktopPane desktopPane = new JDesktopPane();
            desktop.add(desktopPane);
            desktopPane.setBounds(0, 0, DesktopSimulator.this.getWidth(), DesktopSimulator.this.getHeight());

            JInternalFrame internalFrame = new JInternalFrame("文件夹: " + folderName, true, true, true, true);
            internalFrame.setSize(400, 300);
            internalFrame.setLocation(100, 100);
            internalFrame.setLayout(new BorderLayout());

            JTextArea contents = new JTextArea("文件夹内容示例\n文件1.txt\n文件2.txt");
            internalFrame.add(new JScrollPane(contents), BorderLayout.CENTER);

            desktopPane.add(internalFrame);
            internalFrame.setVisible(true);
        }

        private void showFileEditor(String fileName) {
            JDialog dialog = new JDialog(DesktopSimulator.this, "编辑: " + fileName, true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(DesktopSimulator.this);
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
            DesktopSimulator simulator = new DesktopSimulator();
            simulator.setVisible(true);
        });
    }
}
