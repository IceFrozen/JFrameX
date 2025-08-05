package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.utils.ClassUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Meta;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.SettingMenu;
import cn.ximuli.jframex.ui.event.CreateFrameEvent;
import cn.ximuli.jframex.ui.internalJFrame.CommonInternalJFrame;
import cn.ximuli.jframex.ui.util.PermissionUtil;
import org.springframework.core.annotation.AnnotationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UICreator {

    @SuppressWarnings("unchecked")
    public static List<JMenuItem> createJMenuItemForInternalJFrame(Class<? extends JInternalFrame>... classes) {
        List<JMenuItem> result = new ArrayList<>();
        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();

        for (Class<?> aClass : classes) {
            Meta meta = aClass.getAnnotation(Meta.class);
            if (meta != null && !meta.id().isEmpty()) {
                // Check permissions
                if (!PermissionUtil.hasPermission(currentUser, meta.id())) {
                    continue; // Skip menu items without permission
                }
            }

            JMenuItem item = new JMenuItem();
            item.setText(I18nHelper.getMessage(meta.value()));
            item.setIcon(resources.getIcon(meta.icon()));
            item.putClientProperty("class", aClass);
            item.addActionListener(e -> FrameManager.publishEvent(new CreateFrameEvent(aClass)));
            result.add(item);
        }
        return result.stream().filter(Objects::nonNull).toList();
    }

    public static List<JComponent> createSettingPanels() {
        List<JComponent> result = new ArrayList<>();
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);

        try {
            // Scan for classes annotated with SettingMenu
            Set<Class<? extends JComponent>> classes = SpringUtils.scanClasses(Application.APP_COMMON_COMPONENT, JComponent.class);
            for (Class<? extends JComponent> aClass : classes) {
                SettingMenu settingMenu = AnnotationUtils.findAnnotation(aClass, SettingMenu.class);
                if (settingMenu != null) {
                    // Instantiate the component with ResourceLoaderManager
                    JComponent component = ClassUtil.newInstance(aClass, new Class[]{ResourceLoaderManager.class}, new Object[]{resources});
                    component.setName(settingMenu.value());
                    component.setToolTipText(settingMenu.toolTipText());
                    result.add(component);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to scan or instantiate setting panel classes", e);
        }

        // Sort components by the order attribute of their SettingMenu annotation
        return result.stream()
                .filter(Objects::nonNull)
                .sorted((c1, c2) -> {
                    SettingMenu menu1 = c1.getClass().getAnnotation(SettingMenu.class);
                    SettingMenu menu2 = c2.getClass().getAnnotation(SettingMenu.class);
                    return Integer.compare(menu1.order(), menu2.order());
                })
                .collect(Collectors.toList());
    }


    public static List<CommonInternalJFrame> createCommonInternalJFrame(DesktopPanel desktopPanel, Class<? extends CommonInternalJFrame>... classes) {
        List<CommonInternalJFrame> internalJFrames = new ArrayList<>();
        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();

        for (Class<? extends CommonInternalJFrame> aClass : classes) {
            Meta meta = aClass.getAnnotation(Meta.class);
            if (meta != null && !meta.id().isEmpty()) {
                // Check permissions
                if (!PermissionUtil.hasPermission(currentUser, meta.id())) {
                    continue; // Skip internal frames without permission
                }
            }
            CommonInternalJFrame commonInternalJFrame = ClassUtil.newInstance(aClass, new Class[]{ResourceLoaderManager.class, DesktopPanel.class}, new Object[]{resources, desktopPanel});
            commonInternalJFrame.refreshUI();
            // Set permission control
            if (meta != null && !meta.id().isEmpty()) {
                PermissionUtil.setComponentVisibility(currentUser, commonInternalJFrame, meta.id());
            }

            internalJFrames.add(commonInternalJFrame);
        }
        return internalJFrames;
    }


    public static List<JMenu> createJMenuList() throws ClassNotFoundException {
        List<JMenu> menuList = new ArrayList<>();
        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        Set<Class<? extends JMenu>> classes = SpringUtils.scanClasses(Application.APP_COMMON_COMPONENT, JMenu.class);

        for (Class<? extends JMenu> aClass : classes) {
            Meta meta = aClass.getAnnotation(Meta.class);
            if (meta != null && !meta.id().isEmpty()) {
                if (!PermissionUtil.hasPermission(currentUser, meta.id())) {
                    continue;
                }
            }
            JMenu jMenu = ClassUtil.newInstance(aClass, new Class[]{ResourceLoaderManager.class}, new Object[]{resources});

            // Set permission control
            if (meta != null && !meta.id().isEmpty()) {
                PermissionUtil.setComponentVisibility(currentUser, jMenu, meta.id());
            }
            menuList.add(jMenu);
        }
        return menuList;
    }

    public static JMenuItem createJMenuItem(String id,
                                            String textKey,
                                            String iconKey,
                                            int key,
                                            char mnemonic,
                                            ActionListener actionListener) {
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        if (!PermissionUtil.hasPermission(currentUser, id)) {
            return null;
        }

        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);

        JMenuItem item = new JMenuItem();
        item.setText(I18nHelper.getMessage(textKey));
        item.setIcon(resources.getIcon(iconKey));
        item.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        item.setMnemonic(mnemonic);
        item.addActionListener(actionListener);
        item.putClientProperty(cn.ximuli.jframex.ui.component.menu.MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        return item;

    }

    public static JMenu createJMenu(String id, String text) {
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        if (!PermissionUtil.hasPermission(currentUser, id)) {
            return null;
        }

        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);
        JMenu jmenu = new JMenu();
        jmenu.setText(I18nHelper.getMessage(id));
        jmenu.setIcon(resources.getIcon(text));
        return jmenu;
    }

    public static JCheckBoxMenuItem createJCheckBoxMenuItem(String id, String string, int key, char mnemonic, ActionListener actionListener) {
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        if (!PermissionUtil.hasPermission(currentUser, id)) {
            return null;
        }

        JCheckBoxMenuItem toolBarCheckBook = new JCheckBoxMenuItem(I18nHelper.getMessage("app.menu.view.show.toolbar"));
        toolBarCheckBook.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        toolBarCheckBook.setSelected(true);
        toolBarCheckBook.setMnemonic(mnemonic);
        toolBarCheckBook.addActionListener(actionListener);
        return toolBarCheckBook;
    }

    public static ButtonGroup createButtonGroup(String id) {
        LoggedInUser currentUser = FrameManager.getCurrentUISession().getCurrentUser();
        if (!PermissionUtil.hasPermission(currentUser, id)) {
            return null;
        }
        return new ButtonGroup();

    }
}
