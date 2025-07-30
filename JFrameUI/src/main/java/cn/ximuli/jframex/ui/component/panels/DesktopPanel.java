package cn.ximuli.jframex.ui.component.panels;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.ThemeUIManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

public class DesktopPanel extends JDesktopPane {
    private Image backImage;
    private Image topLeftIcon;
    private Image bottomRightIcon;
    private final ResourceLoaderManager resources;


    public DesktopPanel(ResourceLoaderManager resources) {
        super();
        this.resources = resources;
        updateBackImag(false);
        loadIcons();
        ThemeUIManager.themeChangeListener(() -> updateBackImag(true));
    }

    private void loadIcons() {
        String iconPrefix = ThemeUIManager.isDarkMode() ? "dark" : "light";
        topLeftIcon = resources.getImage("icon_top_left");
        bottomRightIcon = resources.getImage(StringUtil.join("icon_bottom_right_", iconPrefix));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Draw background image without stretching (centered)
        int width = getWidth();
        int height = getHeight();
        if (backImage != null) {
            int imgWidth = backImage.getWidth(this);
            int imgHeight = backImage.getHeight(this);

            // Calculate scaling factor to cover the entire panel
            double scaleX = (double) width / imgWidth;
            double scaleY = (double) height / imgHeight;
            double scale = Math.max(scaleX, scaleY); // Use larger scale to cover panel

            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);

            // Center the image
            int x = (width - scaledWidth) / 2;
            int y = (height - scaledHeight) / 2;

            g2d.drawImage(backImage, x, y, scaledWidth, scaledHeight, this);
        }

        // Draw top-left icon (scaled to 10% of panel width, maintaining aspect ratio)
        if (topLeftIcon != null) {
            int maxIconWidth = width / 10; // 10% of panel width
            int iconWidth = topLeftIcon.getWidth(this);
            int iconHeight = topLeftIcon.getHeight(this);
            double scale = Math.min(1.0, (double) maxIconWidth / iconWidth);
            int scaledWidth = (int) (iconWidth * scale);
            int scaledHeight = (int) (iconHeight * scale);
            g2d.drawImage(topLeftIcon, 10, 10, scaledWidth, scaledHeight, this);
        }

        // Draw bottom-right icon (scaled to 10% of panel width, maintaining aspect ratio)
        if (bottomRightIcon != null) {
            int maxIconWidth = width / 10; // 10% of panel width
            int iconWidth = bottomRightIcon.getWidth(this);
            int iconHeight = bottomRightIcon.getHeight(this);
            double scale = Math.min(1.0, (double) maxIconWidth / iconWidth);
            int scaledWidth = (int) (iconWidth * scale);
            int scaledHeight = (int) (iconHeight * scale);
            int x = width - scaledWidth - 10;
            int y = height - scaledHeight - 10;
            g2d.drawImage(bottomRightIcon, x, y, scaledWidth, scaledHeight, this);
        }

        g2d.dispose();
    }

    public void updateBackImag(boolean reload) {
        String backImageName = StringUtil.join("back_", ThemeUIManager.isDarkMode() ? "dark" : "light");
        backImage = this.resources.getImage(backImageName);
        loadIcons(); // Reload icons when background image updates
        if (reload) {
            repaint();
        }
    }
}