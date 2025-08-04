package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.common.constants.PermissionConstants;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.internalJFrame.*;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.UICreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Mate(value = "app.menu.view", shortKey = KeyEvent.VK_F, order = 3, id = "app.menu.view")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewMenu extends JMenu {
    final ResourceLoaderManager resources;

    public ViewMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createComponentsView();
        createScrollingPopupMenu();
        addSeparator();
        createToolBarCheckBook();
        createShowView();
        createRadioButtonMenuItem();
        createDisabledMenu();
    }

    public void createComponentsView() {
        JMenu components = UICreator.createJMenu(PermissionConstants.APP_MENU_VIEW_COMPONENTS, "app.menu.view.components");
        if (components != null) {
            List<JMenuItem> jMenuItemForInternalJFrame = UICreator.createJMenuItemForInternalJFrame(
                    BasicInternalJFrame.class,
                    CreateNewInternalJFrame.class,
                    OptionInternalJFrame.class,
                    ContainerInternalJFrame.class,
                    DataInternalJFrame.class,
                    TabInternalJFrame.class,
                    ExtrasInternalJFrame.class);
            //---- newMenuItem ----
            jMenuItemForInternalJFrame.forEach(components::add);
            add(components);
        }
    }

    public void createScrollingPopupMenu() {
        JMenu scrollingPopupMenu = UICreator.createJMenu(PermissionConstants.APP_MENU_VIEW_SCROLLING, "app.menu.view.scrolling");
        if (scrollingPopupMenu != null) {
            scrollingPopupMenu.add("Large menus are scrollable");
            scrollingPopupMenu.add("Use mouse wheel to scroll");
            scrollingPopupMenu.add("Or use up/down arrows at top/bottom");
            for (int i = 1; i <= 100; i++) {
                scrollingPopupMenu.add("Item " + i);
            }
            add(scrollingPopupMenu);
        }
    }


    public void createShowView() {
        JMenu showView = UICreator.createJMenu(PermissionConstants.APP_MENU_VIEW_SHOW_VIEW, "app.menu.view.show.view");
        if (showView != null) {
            JMenu subViewsMenu = new JMenu(I18nHelper.getMessage("app.menu.view.sub.view"));
            JMenu subSubViewsMenu = new JMenu(I18nHelper.getMessage("app.menu.view.sub.sub.view"));
            JMenuItem errorLogViewMenuItem = new JMenuItem(I18nHelper.getMessage("app.menu.view.sub.sub.view.error.log"));
            JMenuItem searchViewMenuItem = new JMenuItem(I18nHelper.getMessage("app.menu.view.search"));
            JMenuItem projectViewMenuItem = new JMenuItem(I18nHelper.getMessage("app.menu.view.project"));
            JMenuItem structureViewMenuItem = new JMenuItem(I18nHelper.getMessage("app.menu.view.structure"));
            JMenuItem propertiesViewMenuItem = new JMenuItem(I18nHelper.getMessage("app.menu.view.properties"));
            // 创建子菜单

            subViewsMenu.add(subSubViewsMenu);
            showView.add(subViewsMenu);
            //---- sub view - sub sub view - errorLogViewMenuItem ----

            errorLogViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
            subSubViewsMenu.add(errorLogViewMenuItem);
            //---- sub view - sub sub view -searchViewMenuItem ----
            searchViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
            subSubViewsMenu.add(searchViewMenuItem);

            //---- projectViewMenuItem ----
            projectViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
            showView.add(projectViewMenuItem);
            structureViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
            showView.add(structureViewMenuItem);
            //---- propertiesViewMenuItem ----
            propertiesViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
            showView.add(propertiesViewMenuItem);

            add(showView);
        }
    }

    public void createToolBarCheckBook() {
        JCheckBoxMenuItem toolBarCheckBook = UICreator.createJCheckBoxMenuItem(PermissionConstants.APP_MENU_VIEW_SHOW_TOOLBAR, "app.menu.view.show.toolbar", KeyEvent.VK_T, 'T', e -> menuItemActionPerformed(e));
        if (toolBarCheckBook != null) {
            toolBarCheckBook.setSelected(true);
            add(toolBarCheckBook);
        }
    }

    private void menuItemActionPerformed(ActionEvent e) {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        FrameManager.showMessageJOptionPane(mainFrame, "Menu Item", e);
    }

    private JMenu createDisabledMenu() {
        JMenu disabledMenu = new JMenu(I18nHelper.getMessage("app.menu.view.disabled"));
        disabledMenu.setEnabled(false);
        return disabledMenu;
    }

    private void createRadioButtonMenuItem() {
        ButtonGroup buttonGroup  = UICreator.createButtonGroup(PermissionConstants.APP_MENU_VIEW_BUTTON_GROUP);
        if (buttonGroup != null) {
            JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
            JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
            JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();

            buttonGroup.add(radioButtonMenuItem1);
            buttonGroup.add(radioButtonMenuItem2);
            buttonGroup.add(radioButtonMenuItem3);

            //---- radioButtonMenuItem1 ----
            radioButtonMenuItem1.setText(I18nHelper.getMessage("app.menu.view.radio.one"));
            radioButtonMenuItem1.setSelected(true);
            radioButtonMenuItem1.setMnemonic('D');
            radioButtonMenuItem1.addActionListener(e -> menuItemActionPerformed(e));


            //---- radioButtonMenuItem2 ----
            radioButtonMenuItem2.setText(I18nHelper.getMessage("app.menu.view.radio.two"));
            radioButtonMenuItem2.setMnemonic('S');
            radioButtonMenuItem2.addActionListener(e -> menuItemActionPerformed(e));

            //---- radioButtonMenuItem3 ----
            radioButtonMenuItem3.setText(I18nHelper.getMessage("app.menu.view.radio.three"));
            radioButtonMenuItem3.setMnemonic('L');
            radioButtonMenuItem3.addActionListener(e -> menuItemActionPerformed(e));
            add(radioButtonMenuItem1);
            add(radioButtonMenuItem2);
            add(radioButtonMenuItem3);
        }
    }
}