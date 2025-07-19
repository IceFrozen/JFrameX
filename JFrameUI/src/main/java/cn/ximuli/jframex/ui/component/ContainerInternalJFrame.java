package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.panels.ContainerComponentsPanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
@Slf4j
public class ContainerInternalJFrame extends CommonInternalJFrame {
    private JPanel jPanel;

    public ContainerInternalJFrame(ResourceLoaderManager resources, JDesktopPane desktopPane) {
        super(resources, desktopPane);
        setTitle(I18nHelper.getMessage("app.menu.view.components.container"));
        setFrameIcon(super.resources.getIcon("icons/basic_component"));
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
        jPanel = new ContainerComponentsPanel(super.resources);
        jPanel.setBorder(LineBorder.createGrayLineBorder());
        setLayout(new BorderLayout());
        add(jPanel, BorderLayout.CENTER);
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


