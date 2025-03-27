package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.model.Department;
import cn.ximuli.jframex.model.Page;
import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.model.UserType;
import cn.ximuli.jframex.model.constants.Status;
import cn.ximuli.jframex.service.DepartmentService;
import cn.ximuli.jframex.service.UserService;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class UserServiceInternalFrame extends JCommonInternalFrame {
    private JSplitPane splitPane;
    private JTree departmentTree;
    private DefaultTreeModel treeModel;
    private final DepartmentService departmentService;
    private final UserService userService;
    private TablePanel<User> tablePanel;
    private Department selectedDepartment;
    private JPanel departmentPanel;

    public UserServiceInternalFrame(ResourceLoaderManager resources,
                                    JDesktopPane desktopPane,
                                    DepartmentService departmentService,
                                    UserService userService) {
        super(resources, desktopPane);
        this.departmentService = departmentService;
        this.userService = userService;
    }

    @Override
    void initUI() {
        setTitle(I18nHelper.getMessage("app.menu.user.internal.userService.title"));
        setFrameIcon(resources.getIcon("icon/left_arrow"));
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        departmentPanel = initLeftPanel();
        tablePanel = initRightPanel();

        splitPane.setLeftComponent(departmentPanel);
        splitPane.setRightComponent(tablePanel);
        int parentWidth = this.getWidth();
        int desiredWidth = (int) (parentWidth * 1.5);
        splitPane.setDividerLocation(desiredWidth);
        add(splitPane);

        setResizable(true);
        setIconifiable(true);
        setMaximizable(true);
        this.setClosable(true);

        adjustSizeAndPosition(desktopPane);
    }

    private void adjustSizeAndPosition(JDesktopPane desktopPane) {

    }

    private JPanel initLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        departmentTree = createDepartmentTree(new ArrayList<>());
        JScrollPane treeScrollPane = new JScrollPane(departmentTree);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        departmentTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) departmentTree.getLastSelectedPathComponent();
            String dpId = null;
            if (selectedNode != null && selectedNode.getUserObject() instanceof Department department && department != selectedDepartment) {
                selectedDepartment = department;
                dpId = department.getId();
                Page<User> page = tablePanel.getPage();
                page.setPage(1);
                Page<User> userPage = userService.searchUserByPage(dpId, null, page.getPage(), page.getPageSize());
                tablePanel.renderData(userPage);
            }
        });

        return leftPanel;
    }

    private TablePanel<User> initRightPanel() {
        return TablePanel.TablePanelBuilder
                .newBuilder(User.class)
                .searchAction((input, page) -> {
                    String dpId = selectedDepartment == null ? null : selectedDepartment.getId();
                    return userService.searchUserByPage(dpId, input, page.getPage(), page.getPageSize());
                }).converter(UserType.class, (user, value) -> {
                    if (value instanceof UserType userType) {
                        return userType.getName();
                    } else {
                        return value.toString();
                    }
                })
                .converter(Department.class, (user, value) -> {
                    if (value instanceof Department department) {
                        return department.getName();
                    } else {
                        return value.toString();
                    }
                })
                .converter(Status.class, (user, value) -> I18nHelper.i8nConvert((Status) value))
                .build();
    }

    private JTree createDepartmentTree(List<Department> departments) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18nHelper.getMessage("app.message.department.root"));
        treeModel = new DefaultTreeModel(root);
        if (departments != null && !departments.isEmpty()) {
            buildTree(root, departments);
        }
        return new JTree(treeModel);
    }

    private void buildTree(DefaultMutableTreeNode root, List<Department> departments) {
        Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        nodeMap.put("root", root);
        for (Department dept : departments) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dept.getName());
            node.setUserObject(dept);
            nodeMap.put(dept.getId(), node);
        }

        for (Department dept : departments) {
            DefaultMutableTreeNode node = nodeMap.get(dept.getId());
            String parentId = dept.getParentId();
            if (parentId == null || parentId.isEmpty() || !nodeMap.containsKey(parentId)) {
                root.add(node);
            } else {
                DefaultMutableTreeNode parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.add(node);
                }
            }
        }
    }

    // 更新部门树
    public void updateDepartmentTree(List<Department> newDepartments) {
        if (treeModel == null) {
            departmentTree = createDepartmentTree(newDepartments);
            return;
        }

        List<TreePath> expandedPaths = new ArrayList<>();
        for (int i = 0; i < departmentTree.getRowCount(); i++) {
            TreePath path = departmentTree.getPathForRow(i);
            if (departmentTree.isExpanded(path)) {
                expandedPaths.add(path);
            }
        }

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        if (newDepartments != null && !newDepartments.isEmpty()) {
            buildTree(root, newDepartments);
        }

        treeModel.reload();

        for (TreePath path : expandedPaths) {
            departmentTree.expandPath(path);
        }
    }


    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        List<Department> departments = departmentService.queryAllDepartments();
        SwingUtilities.invokeLater(() -> {
            updateDepartmentTree(departments);
        });
    }
}