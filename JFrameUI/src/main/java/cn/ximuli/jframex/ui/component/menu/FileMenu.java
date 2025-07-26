package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.internalJFrame.CreateNewInternalJFrame;
import cn.ximuli.jframex.ui.demo.NewDialog;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.extras.FlatDesktop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@JMenuMeta(value = "app.menu.file.title", shortKey = KeyEvent.VK_F, order = 1)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileMenu extends JMenu {
    ResourceLoaderManager resources;
    @Autowired
    public FileMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem().forEach(this::add);
    }

    public List<JMenuItem> createJMenuItem() {
        List<JMenuItem> items = new ArrayList<>();
        //---- newMenuItem ----
        JMenuItem newMenuItem = new JMenuItem();
        JMenuItem openMenuItem = new JMenuItem();
        JMenuItem saveAsMenuItem= new JMenuItem();
        JMenuItem closeMenuItem= new JMenuItem();
        JMenuItem  exitMenuItem = new JMenuItem();
        newMenuItem.setText(I18nHelper.getMessage("app.menu.file.new"));
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newMenuItem.setIcon(resources.getIcon("icons/newFolder"));
        newMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        newMenuItem.setMnemonic('N');
        newMenuItem.addActionListener(e -> newActionPerformed());
        add(newMenuItem);

        //---- openMenuItem ----
        openMenuItem.setText(I18nHelper.getMessage("app.menu.file.open"));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openMenuItem.setMnemonic('O');
        openMenuItem.setIcon(resources.getIcon("icons/open"));
        openMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        openMenuItem.addActionListener(e -> openActionPerformed());
        add(openMenuItem);

        //---- saveAsMenuItem ----
        saveAsMenuItem.setText(I18nHelper.getMessage("app.menu.file.saveAs"));
        saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAsMenuItem.setMnemonic('S');
        saveAsMenuItem.addActionListener(e -> saveAsActionPerformed());
        saveAsMenuItem.setIcon(resources.getIcon("icons/save"));
        saveAsMenuItem.putClientProperty(MenuBar.MENU_SYNC_TOOL_BAR_KEY, MenuBar.MENU_SYNC_TOOL_BAR_TYPE_SIMPLE);
        add(saveAsMenuItem);
        addSeparator();

        //---- closeMenuItem ----
        closeMenuItem.setText("Close");
        closeMenuItem.setText(I18nHelper.getMessage("app.menu.file.close"));
        closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        closeMenuItem.setMnemonic('C');
        closeMenuItem.addActionListener(e -> menuItemActionPerformed(e));
        closeMenuItem.setIcon(resources.getIcon("icons/save"));
        add(closeMenuItem);
        addSeparator();

        //---- exitMenuItem ----
        exitMenuItem.setText(I18nHelper.getMessage("app.menu.file.exit"));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        exitMenuItem.setMnemonic('X');
        exitMenuItem.addActionListener(e -> exitActionPerformed());
        exitMenuItem.setIcon(resources.getIcon("icons/exit"));
        add(exitMenuItem);


        CreateNewInternalJFrame jInternalFrame = SpringUtils.getBean(CreateNewInternalJFrame.class);
        JMenuItem  createNewItem = new JMenuItem();
        createNewItem.setText(jInternalFrame.getTitle() + "Internal");
        createNewItem.setIcon(jInternalFrame.getFrameIcon());
        createNewItem.putClientProperty("class", jInternalFrame.getClass());
        createNewItem.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(createNewItem)));
        add(createNewItem);

        // integrate into macOS screen menu
        FlatDesktop.setQuitHandler(response -> response.performQuit());
        return items;
    }

    private void newActionPerformed() {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        NewDialog newDialog = new NewDialog(mainFrame);
        newDialog.setVisible(true);
    }

    private void openActionPerformed() {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(mainFrame);
    }

    private void saveAsActionPerformed() {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(mainFrame);
    }

    private void exitActionPerformed() {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        mainFrame.dispose();
    }

    private void menuItemActionPerformed(ActionEvent e) {
        MainFrame mainFrame = SpringUtils.getBean(MainFrame.class);
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE));
    }


}