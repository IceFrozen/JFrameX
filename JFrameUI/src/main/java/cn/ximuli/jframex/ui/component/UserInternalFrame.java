package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.common.utils.DateUtil;
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
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Lazy
public class UserInternalFrame extends JCommonInternalFrame {
    private static final String LEFT_ARROW_ICON_PATH = "icon/left_arrow";
    private static final String USER_INTERNAL_FRAME_TITLE_KEY = "app.menu.user.internal.userService.title";

    private JSplitPane splitPane;
    private JTree departmentTree;
    private JPanel departmentPanel;
    private DefaultTreeModel treeModel;
    private final transient DepartmentService departmentService;
    private final transient UserService userService;
    private TablePanel<User> tablePanel;
    private transient Department selectedDepartment;

    public UserInternalFrame(ResourceLoaderManager resources,
                             JDesktopPane desktopPane,
                             DepartmentService departmentService,
                             UserService userService) {
        super(resources, desktopPane);
        this.departmentService = departmentService;
        this.userService = userService;
    }

    @Override
    void initUI() {
        setTitle(I18nHelper.getMessage(USER_INTERNAL_FRAME_TITLE_KEY));
        setFrameIcon(resources.getIcon(LEFT_ARROW_ICON_PATH));
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
    }

    private JPanel initLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        departmentTree = createDepartmentTree(new ArrayList<>());
        JScrollPane treeScrollPane = new JScrollPane(departmentTree);
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);

        departmentTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) departmentTree.getLastSelectedPathComponent();
            String dpPath = null;
            String searchInput = tablePanel.getSearchField().getText();
            Page<User> page = tablePanel.getPage();
            if (selectedNode != null && selectedNode.getUserObject() instanceof Department department && department != selectedDepartment) {
                selectedDepartment = department;
                dpPath = department.getPath();
                page.setPage(1);
            }
            try {
                Page<User> userPage = userService.searchUserByPage(dpPath, searchInput, page.getPage(), page.getPageSize());
                tablePanel.renderData(userPage);
            } catch (Exception ex) {
                log.error("Failed to search users by page", ex);
                JOptionPane.showMessageDialog(this, "Failed to load users", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return leftPanel;
    }

    private TablePanel<User> initRightPanel() {
        return TablePanel.TablePanelBuilder
                .newBuilder(User.class)
                .searchAction((input, page) -> {
                    String dpPath = selectedDepartment == null ? null : selectedDepartment.getPath();
                    return userService.searchUserByPage(dpPath, input, page.getPage(), page.getPageSize());
                })
                .converter(UserType.class, this::convertUserType)
                .converter(Department.class, this::convertDepartment)
                .converter(Status.class, this::convertStatus)
                .converter(LocalDateTime.class, (user, value) -> DateUtil.formatTime((LocalDateTime) value, DateUtil.DEFAULT_PATTERN))
                .build();
    }

    private String convertUserType(User user, Object value) {
        if (value instanceof UserType userType) {
            return userType.getName();
        } else {
            return value.toString();
        }
    }

    private String convertDepartment(User user, Object value) {
        if (value instanceof Department department) {
            return department.getName();
        } else {
            return value.toString();
        }
    }

    private String convertStatus(User user, Object value) {
        return I18nHelper.i8nConvert((Status) value);
    }

    private JTree createDepartmentTree(List<Department> departments) {
        Department rootDepartment = departmentService.getRootDepartment();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootDepartment);
        departments.remove(rootDepartment);
        treeModel = new DefaultTreeModel(root);
        if (!departments.isEmpty()) {
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

    public void updateDepartmentTree(List<Department> newDepartments) {
        if (treeModel == null) {
            departmentTree = createDepartmentTree(newDepartments);
            return;
        }

        Department rootDepartment = departmentService.getRootDepartment();
        newDepartments.remove(rootDepartment);

        List<TreePath> expandedPaths = new ArrayList<>();
        for (int i = 0; i < departmentTree.getRowCount(); i++) {
            TreePath path = departmentTree.getPathForRow(i);
            if (departmentTree.isExpanded(path)) {
                expandedPaths.add(path);
            }
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();

        if (!newDepartments.isEmpty()) {
            buildTree(root, newDepartments);
        }
        treeModel.reload();

        for (TreePath path : expandedPaths) {
            departmentTree.expandPath(path);
        }
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        try {
            List<Department> departments = departmentService.queryAllDepartments();
            SwingUtilities.invokeLater(() -> updateDepartmentTree(departments));
        } catch (Exception ex) {
            log.error("Failed to query departments", ex);
            JOptionPane.showMessageDialog(this, "Failed to load departments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
