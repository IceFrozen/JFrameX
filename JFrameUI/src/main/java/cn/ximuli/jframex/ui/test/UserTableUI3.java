package cn.ximuli.jframex.ui.test;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserTableUI3 {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("用户管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        // 创建分隔面板，左边是部门树，右边是用户管理
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 左边：部门树
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        JTree departmentTree = createDepartmentTree();
        JScrollPane treeScrollPane = new JScrollPane(departmentTree);

        // 部门树搜索
        JPanel treeSearchPanel = new JPanel();
        treeSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel treeSearchLabel = new JLabel("部门搜索:");
        JTextField treeSearchField = new JTextField(15);
        JButton searchTreeButton = new JButton("搜索");

        treeSearchPanel.add(treeSearchLabel);
        treeSearchPanel.add(treeSearchField);
        treeSearchPanel.add(searchTreeButton);

        leftPanel.add(treeSearchPanel, BorderLayout.NORTH);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        // 右边：用户管理界面
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // 上方的搜索框及过滤项
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("用户账号:");
        JTextField searchField = new JTextField(15);
        topPanel.add(searchLabel);
        topPanel.add(searchField);

        // 用户类型选择框
        JLabel userTypeLabel = new JLabel("用户类型:");
        String[] userTypes = {"全部", "管理员", "普通用户"};
        JComboBox<String> userTypeCombo = new JComboBox<>(userTypes);
        topPanel.add(userTypeLabel);
        topPanel.add(userTypeCombo);

        // 搜索按钮
        JButton searchButton = new JButton("搜索");
        topPanel.add(searchButton);

        rightPanel.add(topPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columnNames = {"用户账号", "手机号", "状态", "创建时间", "操作"};
        Object[][] data = {
                {"admin", "15888888888", "启用", "2023-01-14 21:24:42", "编辑 | 删除"},
                {"ry", "15666666666", "禁用", "2023-01-14 21:24:42", "编辑 | 删除"}
        };
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 分页控件
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));  // 页数放到右边
        JLabel pageLabel = new JLabel("共 1 页");
        JTextField pageTextField = new JTextField(5); // 页码输入框
        JButton goToPageButton = new JButton("跳转");
        JButton prePage = new JButton("上一页");
        JButton nextPate = new JButton("下一页");

        bottomPanel.add(prePage);
        bottomPanel.add(pageLabel);
        bottomPanel.add(pageTextField);
        bottomPanel.add(goToPageButton);
        bottomPanel.add(nextPate);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 为搜索按钮添加功能
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                String userType = (String) userTypeCombo.getSelectedItem();
                // 可以根据条件进行数据过滤，例如更新表格数据
                System.out.println("搜索条件: " + searchText + " | 用户类型: " + userType);
            }
        });

        // 分页按钮功能（仅为演示，功能尚未实现）
        goToPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pageText = pageTextField.getText();
                try {
                    int page = Integer.parseInt(pageText);
                    System.out.println("跳转到第 " + page + " 页");
                    // 可以根据页数动态加载数据
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "请输入有效的页码", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 为部门树的搜索按钮添加功能
        searchTreeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String departmentSearchText = treeSearchField.getText();
                // 进行部门搜索操作，根据输入的部门名称过滤树
                System.out.println("搜索部门: " + departmentSearchText);
                // 这里可以实现基于输入过滤树的功能
            }
        });

        // 将左边部门树和右边用户管理面板加入到分隔面板
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        frame.add(splitPane);

        // 显示窗口
        frame.setVisible(true);
    }

    // 创建部门树
    private static JTree createDepartmentTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("公司");

        DefaultMutableTreeNode department1 = new DefaultMutableTreeNode("人力资源部");
        department1.add(new DefaultMutableTreeNode("招聘组"));
        department1.add(new DefaultMutableTreeNode("培训组"));
        root.add(department1);

        DefaultMutableTreeNode department2 = new DefaultMutableTreeNode("技术部");
        department2.add(new DefaultMutableTreeNode("前端组"));
        department2.add(new DefaultMutableTreeNode("后端组"));
        root.add(department2);

        DefaultMutableTreeNode department3 = new DefaultMutableTreeNode("销售部");
        department3.add(new DefaultMutableTreeNode("国内销售组"));
        department3.add(new DefaultMutableTreeNode("国际销售组"));
        root.add(department3);

        // 创建树并设置模型
        JTree tree = new JTree(root);
        return tree;
    }
}