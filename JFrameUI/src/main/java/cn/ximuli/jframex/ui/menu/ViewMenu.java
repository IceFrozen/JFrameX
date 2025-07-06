package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.BasicInternalFrame;
import cn.ximuli.jframex.ui.component.UserInternalFrame;
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


        BasicInternalFrame jInternalFrame = SpringUtils.getBean(BasicInternalFrame.class);
        basicComponents.setText(jInternalFrame.getTitle());
        basicComponents.setIcon(jInternalFrame.getFrameIcon());
        basicComponents.putClientProperty("class", jInternalFrame.getClass());
        basicComponents.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(basicComponents)));

        components.add(basicComponents);
        components.add(containerComponents);
        components.add(dataComponents);
        components.add(tabComponents);







        //basicComponents.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        add(components);


        return items;

    }

}