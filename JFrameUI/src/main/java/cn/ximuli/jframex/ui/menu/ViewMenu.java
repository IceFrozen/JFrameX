package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.*;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@JMenuMeta(value = "app.menu.view.title", shortKey = KeyEvent.VK_F, order = 3)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewMenu extends JMenu {
    final ResourceLoaderManager resources;
    @Autowired
    public ViewMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem();
    }

    public List<JMenuItem> createJMenuItem() {
        List<JMenuItem> items = new ArrayList<>();
        //---- newMenuItem ----
        JMenu components = new JMenu(I18nHelper.getMessage("app.menu.view.components"));
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
        //basicComponents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        add(components);
        return items;
    }
}