package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class DesktopPanel extends JDesktopPane {
    private static final long serialVersionUID = 1L;
    private Image backImage;
    private final ResourceLoaderManager resources;

    @Autowired
    public DesktopPanel(ResourceLoaderManager resources) {
        super();
        this.resources = resources;
        updateBackImag(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = this.getHeight();
        g.drawImage(backImage, 0, 0, width, height, this);
    }

    public void updateBackImag(boolean reload) {
        String backImageName = StringUtil.join("back_", ThemeUIManager.isDarkMode() ? "dark" : "light");
        backImage = this.resources.getImage(backImageName);
        if (reload) {
            repaint();
        }
    }

}

