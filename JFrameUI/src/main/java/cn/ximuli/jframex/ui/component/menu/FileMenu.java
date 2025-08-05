package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.common.constants.PermissionConstants;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.internalJFrame.CreateNewInternalJFrame;
import cn.ximuli.jframex.ui.demo.NewDialog;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.UICreator;
import com.formdev.flatlaf.extras.FlatDesktop;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Meta(value = "app.menu.file", shortKey = KeyEvent.VK_F, order = 0, id = "app.menu.file")
public class FileMenu extends JMenu {
    final ResourceLoaderManager resources;

    public FileMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem();
    }

    public void createJMenuItem() {
        List<JMenuItem> jMenuItems = new ArrayList<>();
        JMenuItem file = UICreator.createJMenuItem(PermissionConstants.APP_MENU_FILE, "app.menu.file.new", "icons/newFolder", KeyEvent.VK_N, 'N', e -> newActionPerformed());
        JMenuItem openMenuItem = UICreator.createJMenuItem(PermissionConstants.APP_MENU_FILE_OPEN, "app.menu.file.open", "icons/open", KeyEvent.VK_O, 'O', e -> openActionPerformed());
        JMenuItem saveAsMenuItem = UICreator.createJMenuItem(PermissionConstants.APP_MENU_FILE_SAVE_AS, "app.menu.file.saveAs", "icons/save", KeyEvent.VK_S, 'S', e -> saveAsActionPerformed());
        JMenuItem closeMenuItem = UICreator.createJMenuItem(PermissionConstants.APP_MENU_FILE_CLOSE, "app.menu.file.close", "icons/save", KeyEvent.VK_W, 'C', e -> menuItemActionPerformed(e));
        JMenuItem exitMenuItem = UICreator.createJMenuItem(PermissionConstants.APP_MENU_FILE_EXIT, "app.menu.file.exit", "icons/exit", KeyEvent.VK_Q, 'X', e -> exitActionPerformed());

        jMenuItems.add(file);
        jMenuItems.add(openMenuItem);
        jMenuItems.add(saveAsMenuItem);
        jMenuItems.add(closeMenuItem);
        jMenuItems.add(exitMenuItem);


        for (JMenuItem item : jMenuItems) {
            add(item);
        }
        List<JMenuItem> internalJFrameItems = UICreator.createJMenuItemForInternalJFrame(CreateNewInternalJFrame.class);
        for (JMenuItem internalJFrameItem : internalJFrameItems) {
            add(internalJFrameItem);
        }

        // integrate into macOS screen menu
        FlatDesktop.setQuitHandler(response -> response.performQuit());

    }

    private void newActionPerformed() {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        NewDialog newDialog = new NewDialog(mainFrame);
        newDialog.setVisible(true);
    }

    private void openActionPerformed() {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(mainFrame);
    }

    private void saveAsActionPerformed() {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(mainFrame);
    }

    private void exitActionPerformed() {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        mainFrame.dispose();
        System.exit(0);
    }

    private void menuItemActionPerformed(ActionEvent e) {
        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE));
    }


}