package cn.ximuli.jframex.ui.menu;


import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
@Slf4j
@Component
@JMenuIcon("icon/jinhuodan")
@JMenuMeta(value = "app.menu.file", shortKey = KeyEvent.VK_F)
public class FileMenu extends JMenu {

    @Autowired
    ResourceLoaderManager resources;

    @PostConstruct
    public void init() {
        createJMenuItem().forEach(this::add);
    }

    public List<JMenuItem> createJMenuItem() {
        JMenuItem item = new JMenuItem();
        item.setText("item");
        item.setIcon(resources.getIcon("icon/jinhuodan"));
        item.addActionListener(e -> {});
        return Collections.singletonList(item);
    }
}
