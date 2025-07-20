package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.utils.Formatter;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.panels.DesktopPanel;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Objects;

/**
 * A splash screen window displaying a scaled image and a progress bar.
 * This class is a singleton and manages the application's loading progress.
 */
@Slf4j
public class AppSplashScreen extends JWindow {
    @Getter
    private final JProgressBar progressBar;
    private final JLabel splashLabel;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private AppSplashScreen() {
        setSize(MainFrame.getScreenRatioSize());
        setAlwaysOnTop(false);
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow][]"));

        ImageIcon icon = loadSplashImage();
        splashLabel = new JLabel(icon);
        splashLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        add(splashLabel, "grow, center"); // Center the image label
        add(progressBar, "growx, dock south"); // Dock progress bar at the bottom
        pack();
        centerOnScreen();
    }

    /**
     * Closes and disposes of the splash screen.
     *
     * @return true if the splash screen was closed, false otherwise
     */
    public static boolean close() {
        AppSplashScreen instance = SingletonHolder.INSTANCE;
        instance.setVisible(false);
        instance.dispose();
        return true;
    }

    /**
     * Loads and scales the splash image based on the window size.
     *
     * @return the scaled ImageIcon, or an empty ImageIcon if loading fails
     */
    private ImageIcon loadSplashImage() {
        URL url = DesktopPanel.class.getResource("/style/splash.png");
        if (url == null) {
            log.error("Splash image not found at /style/splash.png");
            return new ImageIcon();
        }

        try {
            ImageIcon icon = new ImageIcon(url);
            Image image = icon.getImage();
            if (image == null) {
                log.error("Failed to load splash image: null image");
                return new ImageIcon();
            }

            Dimension windowSize = MainFrame.getScreenRatioSize();
            int windowWidth = windowSize.width;
            int windowHeight = windowSize.height - 30; // Reserve space for progress bar

            // Calculate scaling factor to maintain aspect ratio
            int imgWidth = icon.getIconWidth();
            int imgHeight = icon.getIconHeight();
            if (imgWidth <= 0 || imgHeight <= 0) {
                log.error("Invalid image dimensions: width={} height={}", imgWidth, imgHeight);
                return new ImageIcon();
            }

            double scale = Math.min((double) windowWidth / imgWidth, (double) windowHeight / imgHeight);
            int scaledWidth = (int) (imgWidth * scale);
            int scaledHeight = (int) (imgHeight * scale);

            // Scale the image smoothly
            Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            log.error("Failed to load or scale splash image: {}", e.getMessage());
            return new ImageIcon();
        }
    }

    /**
     * Centers the splash screen on the screen.
     */
    private void centerOnScreen() {
        setLocationRelativeTo(null);
    }

    /**
     * Updates the progress bar based on the provided event.
     *
     * @param event the progress event containing the value and message
     */
    public void updateProgress(ProgressEvent event) {
        Objects.requireNonNull(event, "ProgressEvent cannot be null");
        log.debug("Updating progress: value={}", event.getValue());
        int addedValue = event.getValue();
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            int currentValue = progressBar.getValue();
            int finalValue = Math.min(currentValue + addedValue, progressBar.getMaximum());
            if (finalValue >= progressBar.getMaximum()) {
                FrameManager.publishEvent(new ResourceReadyEvent(""));
            }
            progressBar.setValue(finalValue);
            String message = Formatter.format("{} ({}) ... {}%",
                    I18nHelper.getMessage("app.resource.scan.load.prefix"),
                    event.getMessage(),
                    finalValue);
            progressBar.setString(message);
        });
    }

    /**
     * Updates the progress bar value based on the provided event.
     *
     * @param event the progress event
     */
    public static void setProgressBarValue(ProgressEvent event) {
        AppSplashScreen instance = SingletonHolder.INSTANCE;
        instance.updateProgress(event);
    }

    private static class SingletonHolder {
        private static final AppSplashScreen INSTANCE = new AppSplashScreen();
    }

    /**
     * Gets the singleton instance of AppSplashScreen.
     *
     * @return the singleton instance
     */
    public static AppSplashScreen getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
