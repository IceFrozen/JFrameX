package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.panels.TabsComponentsPanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
@Slf4j
public class TabInternalJFrame extends CommonInternalJFrame {

    private TabsComponentsPanel tabsComponentsPanel;

    public TabInternalJFrame(ResourceLoaderManager resources, JDesktopPane desktopPane, TabsComponentsPanel tabsComponentsPanel) {
        super(resources, desktopPane);
        this.tabsComponentsPanel = tabsComponentsPanel;
        setTitle(I18nHelper.getMessage("app.menu.view.components.tab"));
        setFrameIcon(super.resources.getIcon("icons/tab_component"));
        // Add component listener to adjust size when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }

    @Override
    protected void initUI() {
        tabsComponentsPanel = new TabsComponentsPanel(super.resources);
        tabsComponentsPanel.setBorder(LineBorder.createGrayLineBorder());
        setLayout(new BorderLayout());
        add(tabsComponentsPanel, BorderLayout.CENTER);
    }

    private void adjustFrameSize() {
        // Get the preferred size of the jPanel
        Dimension panelSize = tabsComponentsPanel.getPreferredSize();
        // Add some padding for the frame's borders and title bar
        int padding = 20;
        int width = panelSize.width + padding;
        int height = panelSize.height + padding;

        // Ensure the size doesn't exceed the desktop pane's dimensions
        Dimension desktopSize = desktopPane.getSize();
        width = Math.min(width, desktopSize.width - 20);
        height = Math.min(height, desktopSize.height - 20);

        // Set the frame size
        setSize(width, height);

        // Center the frame within the desktop pane
        int x = (desktopSize.width - width) / 2;
        int y = (desktopSize.height - height) / 2;
        setLocation(x, y);
    }

}


