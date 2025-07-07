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


        BasicInternalFrame basicInternalFrame = SpringUtils.getBean(BasicInternalFrame.class);
        basicComponents.setText(basicInternalFrame.getTitle());
        basicComponents.setIcon(basicInternalFrame.getFrameIcon());
        basicComponents.putClientProperty("class", basicInternalFrame.getClass());
        basicComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(basicComponents)));

        ContainerInternalFrame containerInternalFrame = SpringUtils.getBean(ContainerInternalFrame.class);
        containerComponents.setText(containerInternalFrame.getTitle());
        containerComponents.setIcon(containerInternalFrame.getFrameIcon());
        containerComponents.putClientProperty("class", containerInternalFrame.getClass());
        containerComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(containerComponents)));

        DataInternalFrame dataInternalFrame = SpringUtils.getBean(DataInternalFrame.class);
        dataComponents.setText(dataInternalFrame.getTitle());
        dataComponents.setIcon(dataInternalFrame.getFrameIcon());
        dataComponents.putClientProperty("class", dataInternalFrame.getClass());
        dataComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(dataComponents)));


        TabInternalFrame tabInternalFrame = SpringUtils.getBean(TabInternalFrame.class);
        tabComponents.setText(tabInternalFrame.getTitle());
        tabComponents.setIcon(tabInternalFrame.getFrameIcon());
        tabComponents.putClientProperty("class", tabInternalFrame.getClass());
        tabComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(tabComponents)));

        OptionInternalFrame optionInternalFrame = SpringUtils.getBean(OptionInternalFrame.class);
        optionComponents.setText(optionInternalFrame.getTitle());
        optionComponents.setIcon(optionInternalFrame.getFrameIcon());
        optionComponents.putClientProperty("class", optionInternalFrame.getClass());
        optionComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(optionComponents)));

        components.add(basicComponents);
        components.add(containerComponents);
        components.add(dataComponents);
        components.add(tabComponents);
        components.add(optionComponents);


        //basicComponents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        add(components);


        return items;

    }

}