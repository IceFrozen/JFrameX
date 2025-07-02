package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
@Component
@JMenuMeta(value = "app.menu.view.title", shortKey = KeyEvent.VK_F, order = 3)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewMenu extends JMenu {
    ResourceLoaderManager resources;
    @Autowired
    public ViewMenu(ResourceLoaderManager resources) {
        this.resources = resources;
    }

    public List<JMenuItem> createJMenuItem() {
        return null;
    }

}