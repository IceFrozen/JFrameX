package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.utils.ClassUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.SettingMenu;
import cn.ximuli.jframex.ui.event.CreateFrameEvent;
import cn.ximuli.jframex.ui.internalJFrame.CommonInternalJFrame;
import org.springframework.core.annotation.AnnotationUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class UICreator {

    public static List<JMenuItem> createJMenuItemForInternalJFrame(Class<? extends JInternalFrame>... classes) {
        List<JMenuItem> result = new ArrayList<>();
        ResourceLoaderManager resources = SpringUtils.getBean(ResourceLoaderManager.class);
        for (Class<?> aClass : classes) {
            JMenuItem item = new JMenuItem();
            item.setText(I18nHelper.getMessage(aClass.getAnnotation(Mate.class).value()));
            item.setIcon(resources.getIcon(aClass.getAnnotation(Mate.class).icon()));
            item.putClientProperty("class", aClass);
            item.addActionListener(e -> FrameManager.publishEvent(new CreateFrameEvent(aClass)));
            result.add(item);
        }
        return result.stream().filter(Objects::nonNull).toList();
    }

    /**
     * Creates a list of JComponent instances annotated with SettingMenu, sorted by their order attribute.
     *
     * @return A sorted list of JComponent instances
     * @throws RuntimeException If an error occurs during class scanning or instantiation
     */
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
            CommonInternalJFrame commonInternalJFrame = ClassUtil.newInstance(aClass, new Class[]{ResourceLoaderManager.class, DesktopPanel.class}, new Object[]{resources, desktopPanel});
            commonInternalJFrame.refreshUI();
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
            JMenu jMenu = ClassUtil.newInstance(aClass, new Class[]{ResourceLoaderManager.class}, new Object[]{resources});
            menuList.add(jMenu);
        }
        return menuList;
    }

}
