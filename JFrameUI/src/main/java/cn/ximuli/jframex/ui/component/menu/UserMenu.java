package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.ui.manager.UICreator;
import cn.ximuli.jframex.ui.internalJFrame.UserInternalJFrame;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;


@Slf4j
@Meta(value = "app.menu.user", order = 6, id = "app.menu.user")
public class UserMenu extends JMenu {
    ResourceLoaderManager resources;

    public UserMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        UICreator.createJMenuItemForInternalJFrame(
                UserInternalJFrame.class
        ).forEach(this::add);
    }
}
