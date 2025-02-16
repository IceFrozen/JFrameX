package cn.ximuli.jframex.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Component
public class DesktopPanel extends JDesktopPane {

    private static final long serialVersionUID = 1L;
    private final Image backImage;

    @Autowired
    public DesktopPanel() {
        super();
        URL url = DesktopPanel.class.getResource("/style/default/back.jpg");
        backImage = new ImageIcon(url).getImage();
    }
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = this.getHeight();
        g.drawImage(backImage, 0, 0, width, height, this);
    }
}

