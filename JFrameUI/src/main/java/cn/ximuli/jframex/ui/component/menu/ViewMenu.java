package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.internalJFrame.*;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
@Component
@JMenuMeta(value = "app.menu.view.title", shortKey = KeyEvent.VK_F, order = 3)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewMenu extends JMenu {
    final ResourceLoaderManager resources;
    private final JMenu scrollingPopupMenu;
    private final JMenu showView;
    private final JMenu components;
    private final JMenu diisabledMenu;
    private final JCheckBoxMenuItem toolBarCheckBook;

    @Autowired
    public ViewMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        components = createComponentsView();
        scrollingPopupMenu = createScrollingPopupMenu();
        toolBarCheckBook = createToolBarCheckBook();
        showView = createShowView();
        diisabledMenu =  createDisabledMenu();
        List<JRadioButtonMenuItem> radioButtonMenuItem = createRadioButtonMenuItem();
        add(scrollingPopupMenu);
        add(toolBarCheckBook);
        add(components);
        add(showView);
        addSeparator();
        radioButtonMenuItem.forEach(this::add);
    }

    public JMenu createComponentsView() {
        JMenu components = new JMenu(I18nHelper.getMessage("app.menu.view.components"));
        //---- newMenuItem ----
        JMenuItem basicComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.basic"));
        JMenuItem containerComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.container"));
        JMenuItem dataComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.data"));
        JMenuItem tabComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.tab"));
        JMenuItem optionComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.option"));
        JMenuItem extrasComponents = new JMenuItem(I18nHelper.getMessage("app.menu.view.components.extras"));


        BasicInternalJFrame basicInternalJFrame = SpringUtils.getBean(BasicInternalJFrame.class);
        basicComponents.setText(basicInternalJFrame.getTitle());
        basicComponents.setIcon(basicInternalJFrame.getFrameIcon());
        basicComponents.putClientProperty("class", basicInternalJFrame.getClass());
        basicComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(basicComponents)));

        ContainerInternalJFrame containerInternalJFrame = SpringUtils.getBean(ContainerInternalJFrame.class);
        containerComponents.setText(containerInternalJFrame.getTitle());
        containerComponents.setIcon(containerInternalJFrame.getFrameIcon());
        containerComponents.putClientProperty("class", containerInternalJFrame.getClass());
        containerComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(containerComponents)));

        DataInternalJFrame dataInternalJFrame = SpringUtils.getBean(DataInternalJFrame.class);
        dataComponents.setText(dataInternalJFrame.getTitle());
        dataComponents.setIcon(dataInternalJFrame.getFrameIcon());
        dataComponents.putClientProperty("class", dataInternalJFrame.getClass());
        dataComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(dataComponents)));

        TabInternalJFrame tabInternalFrame = SpringUtils.getBean(TabInternalJFrame.class);
        tabComponents.setText(tabInternalFrame.getTitle());
        tabComponents.setIcon(tabInternalFrame.getFrameIcon());
        tabComponents.putClientProperty("class", tabInternalFrame.getClass());
        tabComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(tabComponents)));

        OptionInternalJFrame optionInternalFrame = SpringUtils.getBean(OptionInternalJFrame.class);
        optionComponents.setText(optionInternalFrame.getTitle());
        optionComponents.setIcon(optionInternalFrame.getFrameIcon());
        optionComponents.putClientProperty("class", optionInternalFrame.getClass());
        optionComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(optionComponents)));


        ExtrasInternalJFrame extrasInternalFrame = SpringUtils.getBean(ExtrasInternalJFrame.class);
        extrasComponents.setText(extrasInternalFrame.getTitle());
        extrasComponents.setIcon(extrasInternalFrame.getFrameIcon());
        extrasComponents.putClientProperty("class", extrasInternalFrame.getClass());
        extrasComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(extrasComponents)));

        components.add(basicComponents);
        components.add(containerComponents);
        components.add(dataComponents);
        components.add(tabComponents);
        components.add(optionComponents);
        components.add(extrasComponents);
        return components;
    }

    public JMenu createScrollingPopupMenu() {
        JMenu scrollingPopupMenu = new JMenu(I18nHelper.getMessage("app.menu.view.scrolling"));
        scrollingPopupMenu.add("Large menus are scrollable");
        scrollingPopupMenu.add("Use mouse wheel to scroll");
        scrollingPopupMenu.add("Or use up/down arrows at top/bottom");
        for (int i = 1; i <= 100; i++) {
            scrollingPopupMenu.add("Item " + i);
        }
        return scrollingPopupMenu;
    }


    public JMenu createShowView() {
        JMenu showView = new JMenu(I18nHelper.getMessage("app.menu.view.show.view"));
        showView.setMnemonic('V');

        JMenu subViewsMenu = new JMenu();
        JMenu subSubViewsMenu = new JMenu();

        JMenuItem errorLogViewMenuItem = new JMenuItem();
        JMenuItem searchViewMenuItem = new JMenuItem();
        JMenuItem projectViewMenuItem = new JMenuItem();
        JMenuItem structureViewMenuItem = new JMenuItem();
        JMenuItem propertiesViewMenuItem = new JMenuItem();

        //======== subViewsMenu ========

        subViewsMenu.setText(I18nHelper.getMessage("app.menu.view.sub.view"));
        subViewsMenu.setMnemonic('S');
        showView.add(subViewsMenu);

        //======== subSubViewsMenu ========

        subSubViewsMenu.setText(I18nHelper.getMessage("app.menu.view.sub.sub.view"));
        subSubViewsMenu.setMnemonic('U');
        subViewsMenu.add(subSubViewsMenu);

        //---- sub view - sub sub view - errorLogViewMenuItem ----
        errorLogViewMenuItem.setText(I18nHelper.getMessage("app.menu.view.sub.sub.view.error.log"));
        errorLogViewMenuItem.setMnemonic('E');
        errorLogViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        subSubViewsMenu.add(errorLogViewMenuItem);


        //---- sub view - sub sub view -searchViewMenuItem ----
        searchViewMenuItem.setText(I18nHelper.getMessage("app.menu.view.search"));
        searchViewMenuItem.setMnemonic('S');
        searchViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        subSubViewsMenu.add(searchViewMenuItem);


        //---- projectViewMenuItem ----
        projectViewMenuItem.setText(I18nHelper.getMessage("app.menu.view.project"));
        projectViewMenuItem.setMnemonic('P');
        projectViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        showView.add(projectViewMenuItem);

        //---- structureViewMenuItem ----
        structureViewMenuItem.setText(I18nHelper.getMessage("app.menu.view.structure"));
        structureViewMenuItem.setMnemonic('T');
        structureViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        showView.add(structureViewMenuItem);

        //---- propertiesViewMenuItem ----
        propertiesViewMenuItem.setText(I18nHelper.getMessage("app.menu.view.properties"));
        propertiesViewMenuItem.setMnemonic('O');
        propertiesViewMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        showView.add(propertiesViewMenuItem);
        return showView;
    }

    public JCheckBoxMenuItem createToolBarCheckBook() {
        JCheckBoxMenuItem toolBarCheckBook = new JCheckBoxMenuItem(I18nHelper.getMessage("app.menu.view.show.toolbar"));
        toolBarCheckBook.setSelected(true);
        toolBarCheckBook.setMnemonic('T');
        toolBarCheckBook.addActionListener(e -> menuItemActionPerformed(e));
        return toolBarCheckBook;
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

    private  List<JRadioButtonMenuItem> createRadioButtonMenuItem() {

        JRadioButtonMenuItem radioButtonMenuItem1 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem2 = new JRadioButtonMenuItem();
        JRadioButtonMenuItem radioButtonMenuItem3 = new JRadioButtonMenuItem();

        ButtonGroup buttonGroup = new ButtonGroup();
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

        return List.of(radioButtonMenuItem1, radioButtonMenuItem2, radioButtonMenuItem3);

    }
}