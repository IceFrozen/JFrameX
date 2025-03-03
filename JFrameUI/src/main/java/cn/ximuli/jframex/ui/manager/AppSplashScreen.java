package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.utils.Formatter;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.DesktopPanel;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class AppSplashScreen extends JWindow {
    private static AppSplashScreen INSTANCE = new AppSplashScreen();

    private ExecutorService animExecutor = Executors.newSingleThreadExecutor();
    private JProgressBar progressBar;

    public AppSplashScreen() {
        ImageIcon icon = loadSplashImage();
        JLabel splashLabel = new JLabel(icon);
//        Image scaled = icon.getImage().getScaledInstance(
//                (int)(600 * Toolkit.getDefaultToolkit().getScreenResolution()  / 96f),
//                (int)(400 * Toolkit.getDefaultToolkit().getScreenResolution()  / 96f),
//                Image.SCALE_SMOOTH
//        );
//        JLabel splashLabel = new JLabel(new ImageIcon(scaled));
        splashLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        setLayout(new BorderLayout());
        add(splashLabel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.SOUTH);
        setAlwaysOnTop(true);
        pack();
        centerOnScreen();
    }

    public static boolean close() {
        if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
            INSTANCE = null;
            return true;
        }
        return false;
    }

    private ImageIcon loadSplashImage() {
        URL url = DesktopPanel.class.getResource("/splash.jpg");
        return new ImageIcon(url);
    }

    private void centerOnScreen() {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    public static void setProgressBarValue(ProgressEvent event) {
        if (INSTANCE != null) {
            INSTANCE.updateProgress(event);
        }
    }

    public void updateProgress(ProgressEvent event) {
        log.debug("Update progress ... {}", event.getValue());
        int addedValue = event.getValue();
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            int currentValue = progressBar.getValue();
            int finalValue = currentValue + addedValue;
            if (finalValue >= progressBar.getMaximum()) {
                FrameManager.publishEvent(new ResourceReadyEvent(""));
            }
            progressBar.setValue(finalValue);
            String totalMessage = Formatter.format("{}({}) ... {} %", I18nHelper.getMessage("app.resource.scan.load.prefix"), event.getMessage(), finalValue);
            progressBar.setString(totalMessage);
        });
    }
    public static AppSplashScreen getInstance() {
        return INSTANCE;
    }

}