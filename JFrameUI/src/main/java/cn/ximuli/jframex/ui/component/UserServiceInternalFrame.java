package cn.ximuli.jframex.ui.component;
import cn.ximuli.jframex.ui.I18nHelper;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class UserServiceInternalFrame extends JInternalFrame {
    // 成员变量：UI 组件
    private JSplitPane splitPane;
    private JTree departmentTree;
    private JTextField treeSearchField;
    private JButton searchTreeButton;
    private JTextField searchField;
    private JComboBox<String> userTypeCombo;
    private JButton searchButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField pageTextField;
    private JButton goToPageButton;
    private JButton prePage;
    private JButton nextPage;

    // 构造函数
    public UserServiceInternalFrame() {
        super(I18nHelper.getMessage("app.menu.example.internal.userservice.title"), true, true, true, true);
        this.setSize(800, 600);
        initUI();
    }

    // 初始化 UI 组件和布局
    private void initUI() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        departmentTree = createDepartmentTree();
        JScrollPane treeScrollPane = new JScrollPane(departmentTree);


        // 部门树搜索
        JPanel treeSearchPanel = new JPanel();
        treeSearchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftPanel.add(treeSearchPanel, BorderLayout.NORTH);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        // 右边：用户管理界面
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // 上方的搜索框及过滤项
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("用户账号:");
        searchField = new JTextField(15);
        topPanel.add(searchLabel);
        topPanel.add(searchField);

        // 用户类型选择框
        JLabel userTypeLabel = new JLabel("用户类型:");
        String[] userTypes = {"全部", "管理员", "普通用户"};
        userTypeCombo = new JComboBox<>(userTypes);
        topPanel.add(userTypeLabel);
        topPanel.add(userTypeCombo);

        // 搜索按钮
        searchButton = new JButton("搜索");
        topPanel.add(searchButton);

        rightPanel.add(topPanel, BorderLayout.NORTH);

        // 创建表格
        String[] columnNames = {"用户账号", "手机号", "状态", "创建时间", "操作"};
        Object[][] data = {
                {"admin", "15888888888", "启用", "2023-01-14 21:24:42", "编辑 | 删除"},
                {"ry", "15666666666", "禁用", "2023-01-14 21:24:42", "编辑 | 删除"}
        };
        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // 分页控件
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JLabel pageLabel = new JLabel("共 1 页");
        pageTextField = new JTextField(5);
        goToPageButton = new JButton("跳转");
        prePage = new JButton("上一页");
        nextPage = new JButton("下一页");

        bottomPanel.add(prePage);
        bottomPanel.add(pageLabel);
        bottomPanel.add(pageTextField);
        bottomPanel.add(goToPageButton);
        bottomPanel.add(nextPage);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 为搜索按钮添加功能
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText();
                String userType = (String) userTypeCombo.getSelectedItem();
                System.out.println("搜索条件: " + searchText + " | 用户类型: " + userType);
            }
        });

        // 分页按钮功能
        goToPageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pageText = pageTextField.getText();
                try {
                    int page = Integer.parseInt(pageText);
                    System.out.println("跳转到第 " + page + " 页");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(UserServiceInternalFrame.this, "请输入有效的页码", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        // 将左边部门树和右边用户管理面板加入到分隔面板
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane);
    }

    // 创建部门树
    private JTree createDepartmentTree() {
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

        JTree tree = new JTree(root);
        return tree;
    }
}
