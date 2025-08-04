package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Mate;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.panels.DataComponentsPanel;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Mate(value = "app.menu.view.components.data", icon = "icons/data_component", order = 3, id = "app.menu.view.components.data")
@Slf4j
public class DataInternalJFrame extends CommonInternalJFrame {
    private JPanel jPanel;

    public DataInternalJFrame(ResourceLoaderManager resources, DesktopPanel desktopPane) {
        super(resources, desktopPane);
        setTitle(I18nHelper.getMessage(getClass().getAnnotation(Mate.class).value()));
        setFrameIcon(resources.getIcon(getClass().getAnnotation(Mate.class).icon()));

        // Add component listener to adjust size when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }


    public void refreshUI() {
        jPanel = new DataComponentsPanel(resources);
        jPanel.setBorder(LineBorder.createGrayLineBorder());
        setLayout(new BorderLayout());
        add(jPanel, BorderLayout.CENTER);
    }

    @Override
    public void showHint(boolean b) {

    }

    private void adjustFrameSize() {
        // Get the preferred size of the jPanel
        Dimension panelSize = jPanel.getPreferredSize();
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


