package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.common.constants.PermissionConstants;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.UICreator;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Meta(value = "app.menu.edit", shortKey = KeyEvent.VK_E, order = 2, id = "app.menu.edit")
public class EditMenu extends JMenu {
    final ResourceLoaderManager resources;

    public EditMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem();
    }

    public void createJMenuItem() {
        List<JMenuItem> jMenuItems = new ArrayList<>();

        JMenuItem undoMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT,
            "app.menu.edit.undo", 
            "icons/undo", 
            KeyEvent.VK_Z, 
            'U', 
            e -> menuItemActionPerformed(e)
        );

        JMenuItem redoMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT_REDO,
            "app.menu.edit.redo", 
            "icons/redo", 
            KeyEvent.VK_Y, 
            'R', 
            e -> menuItemActionPerformed(e)
        );

        JMenuItem cutMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT_CUT,
            "app.menu.edit.cut", 
            "icons/menu-cut", 
            KeyEvent.VK_X, 
            'C', 
            e -> new DefaultEditorKit.CutAction().actionPerformed(e)
        );

        JMenuItem copyMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT_COPY,
            "app.menu.edit.copy", 
            "icons/copy", 
            KeyEvent.VK_C, 
            'O', 
            e -> new DefaultEditorKit.CopyAction().actionPerformed(e)
        );

        JMenuItem pasteMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT_PASTE,
            "app.menu.edit.paste", 
            "icons/menu-paste", 
            KeyEvent.VK_V, 
            'P', 
            e -> new DefaultEditorKit.PasteAction().actionPerformed(e)
        );

        JMenuItem deleteMenuItem = UICreator.createJMenuItem(
            PermissionConstants.APP_MENU_EDIT_DELETE,
            "app.menu.edit.delete", 
            "icons/delete", 
            KeyEvent.VK_D,
            'D', 
            e -> menuItemActionPerformed(e)
        );

        jMenuItems.add(undoMenuItem);
        jMenuItems.add(redoMenuItem);
        jMenuItems.add(cutMenuItem);
        jMenuItems.add(copyMenuItem);
        jMenuItems.add(pasteMenuItem);
        jMenuItems.add(deleteMenuItem);

        for (JMenuItem item : jMenuItems) {
            if (item != null) {
                add(item);
            }
        }
    }

    private void menuItemActionPerformed(ActionEvent e) {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE));
    }
}
