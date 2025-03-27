package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class DesktopPanel extends JDesktopPane {
    private static final long serialVersionUID = 1L;
    private final Image backImage;

    @Autowired
    public DesktopPanel(ResourceLoaderManager resources) {
        super();
        backImage = resources.getImage("back_img");
    }
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = this.getHeight();
        g.drawImage(backImage, 0, 0, width, height, this);

    }

}

