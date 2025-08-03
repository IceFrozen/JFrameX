package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Mate(value = "app.menu.edit", shortKey = KeyEvent.VK_E, order = 2, id = "app.menu.edit")
public class EditMenu extends JMenu {
    final ResourceLoaderManager resources;

    public EditMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem().forEach(this::add);
    }

    public java.util.List<JMenuItem> createJMenuItem() {
        List<JMenuItem> items = new ArrayList<>();
        //---- undoMenuItem ----
        JMenuItem undoMenuItem = new JMenuItem();
        undoMenuItem.setText(I18nHelper.getMessage("app.menu.edit.undo"));
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        undoMenuItem.setMnemonic('U');

        undoMenuItem.setIcon(resources.getIcon("icons/undo"));
        undoMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        undoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        add(undoMenuItem);

        //---- redoMenuItem ----
        JMenuItem redoMenuItem = new JMenuItem();
        redoMenuItem.setText(I18nHelper.getMessage("app.menu.edit.redo"));
        redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        redoMenuItem.setMnemonic('R');
        redoMenuItem.setIcon(resources.getIcon(("icons/redo")));
        redoMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        redoMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        add(redoMenuItem);
        addSeparator();

        //---- cutMenuItem ----
        JMenuItem cutMenuItem = new JMenuItem();
        cutMenuItem.setAction(new DefaultEditorKit.CutAction());
        cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        cutMenuItem.setMnemonic('C');
        cutMenuItem.setIcon(resources.getIcon(("icons/menu-cut")));
        cutMenuItem.setText(I18nHelper.getMessage("app.menu.edit.cut"));
        cutMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        add(cutMenuItem);
        //---- copyMenuItem ----
        JMenuItem copyMenuItem = new JMenuItem();
        copyMenuItem.setAction(new DefaultEditorKit.CopyAction());
        copyMenuItem.setText(I18nHelper.getMessage("app.menu.edit.copy"));
        copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        copyMenuItem.setMnemonic('O');
        copyMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        copyMenuItem.setIcon(resources.getIcon(("icons/copy")));
        add(copyMenuItem);

        //---- pasteMenuItem ----
        JMenuItem pasteMenuItem = new JMenuItem();
        pasteMenuItem.setAction(new DefaultEditorKit.PasteAction());
        pasteMenuItem.setText(I18nHelper.getMessage("app.menu.edit.paste"));
        pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        pasteMenuItem.setMnemonic('P');
        pasteMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        pasteMenuItem.setIcon(resources.getIcon(("icons/menu-paste")));
        add(pasteMenuItem);
        addSeparator();

        //---- deleteMenuItem ----
        JMenuItem deleteMenuItem = new JMenuItem();
        deleteMenuItem.setText(I18nHelper.getMessage("app.menu.edit.delete"));
        deleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteMenuItem.setMnemonic('D');
        deleteMenuItem.setIcon(resources.getIcon(("icons/delete")));
        deleteMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        deleteMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        add(deleteMenuItem);
        return items;
    }

    private void menuItemActionPerformed(ActionEvent e) {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE));
    }
}
