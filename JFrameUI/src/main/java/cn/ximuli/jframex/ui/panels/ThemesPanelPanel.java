package cn.ximuli.jframex.ui.panels;

import cn.ximuli.jframex.ui.component.intellijthemes.IJThemesPanel;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
@Slf4j
public class ThemesPanelPanel extends JPanel {
    ResourceLoaderManager resources;
    @Getter
    IJThemesPanel themesPanel;

    public ThemesPanelPanel(ResourceLoaderManager resources) {
        this.resources = resources;
        this.themesPanel = new IJThemesPanel(resources);
        JPanel winFullWindowContentButtonsPlaceholder = new JPanel();
        setLayout(new BorderLayout());
        winFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        add(winFullWindowContentButtonsPlaceholder, BorderLayout.NORTH);
        add(themesPanel, BorderLayout.CENTER);
    }
}
