package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatDesktop;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Mate(value = "app.menu.help", shortKey = KeyEvent.VK_F, order = Integer.MAX_VALUE, id = "app.menu.help")
public class HelpMenu extends JMenu {
    ResourceLoaderManager resources;

    public HelpMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        createJMenuItem().forEach(this::add);
    }

    public List<JMenuItem> createJMenuItem() {
        List<JMenuItem> items = new ArrayList<>();
        JMenuItem item = new JMenuItem();
        //---- aboutMenuItem ----
        item.setText(I18nHelper.getMessage("app.menu.help.about"));
        item.setMnemonic('A');
        item.addActionListener(e -> aboutActionPerformed());
        items.add(item);
        FlatDesktop.setAboutHandler(this::aboutActionPerformed);
        return items;
    }

    private void aboutActionPerformed() {
        JLabel titleLabel = new JLabel("FlatLaf Demo");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        String link = "https://www.ximuli.cn";
        JLabel linkLabel = new JLabel("<html><a href=\"#\">" + link + "</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(linkLabel,
                            "Failed to open '" + link + "' in browser.",
                            "About", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });

        MainFrame mainFrame = FrameManager.getCurrentUISession().getMainFrame();
        JOptionPane.showMessageDialog(mainFrame,
                new Object[]{
                        titleLabel,
                        "JFrameX Swing Framework develop by ximulil",
                        " ",
                        "Copyright 2025-" + Year.now() + " Ximuli Software",
                        linkLabel,
                },
                "About", JOptionPane.PLAIN_MESSAGE);
    }

}
