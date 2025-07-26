package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.OptionPanePanel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@Component
@Slf4j
public class OptionInternalJFrame extends CommonInternalJFrame {
    private JPanel jPanel;

    public OptionInternalJFrame(ResourceLoaderManager resources, DesktopPanel desktopPane) {
        super(resources, desktopPane);
        setTitle(I18nHelper.getMessage("app.menu.view.components.option"));
        setFrameIcon(super.resources.getIcon("icons/infoOutline"));
        // Add component listener to adjust size when shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                adjustFrameSize();
            }
        });
    }

    @Override
    protected void refleshUI() {
        jPanel = new OptionPanePanel(super.resources);
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


        // Ensure the size doesn't exceed the desktop pane's dimensions
        Dimension desktopSize = desktopPane.getSize();
        width = Math.min(width, desktopSize.width - 20);
        int height = desktopSize.height / 2;

        // Set the frame size
        setSize(width, height);

        // Center the frame within the desktop pane
        int x = (desktopSize.width - width) / 2;
        int y = (desktopSize.height - height) / 2;
        setLocation(x, y);
    }


}


