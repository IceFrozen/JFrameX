package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.List;

@Slf4j
@Component
@JMenuMeta(value = "app.menu.options.title", shortKey = KeyEvent.VK_F, order = 5)
public class OptionsMenu extends JMenu {
    ResourceLoaderManager resources;

    @Autowired
    public OptionsMenu(ResourceLoaderManager resources) {
        this.resources = resources;
    }

    public List<JMenuItem> createJMenuItem() {
        return null;
    }
}
