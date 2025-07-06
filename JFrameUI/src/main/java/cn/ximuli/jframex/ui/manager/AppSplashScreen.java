package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.common.utils.Formatter;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.component.DesktopPanel;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

@Slf4j
public class AppSplashScreen extends JWindow {
    private static AppSplashScreen INSTANCE = new AppSplashScreen();
    private JProgressBar progressBar;
    private JLabel splashLabel;

    public AppSplashScreen() {
        // 初始化窗口
        setSize(MainFrame.getScreenRatioSize());
        setAlwaysOnTop(true);
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow][]")); // MigLayout: 填充窗口，垂直布局

        // 加载并缩放图片
        ImageIcon icon = loadSplashImage();
        splashLabel = new JLabel(icon);
        splashLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // 初始化进度条
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");

        // 添加组件到窗口
        add(splashLabel, "grow, center"); // 图片标签填充窗口并居中
        add(progressBar, "growx, dock south"); // 进度条填充宽度，固定在底部

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
        URL url = DesktopPanel.class.getResource("/style/splash.png");
        if (url == null) {
            log.error("Splash image not found at /style/splash.png");
            return new ImageIcon(); // 返回空图标以避免空指针
        }

        ImageIcon icon = new ImageIcon(url);
        Image image = icon.getImage();

        // 获取窗口的尺寸
        Dimension windowSize = MainFrame.getScreenRatioSize();
        int windowWidth = windowSize.width;
        int windowHeight = windowSize.height - 30; // 留出进度条空间

        // 计算图片的缩放比例，保持纵横比
        int imgWidth = icon.getIconWidth();
        int imgHeight = icon.getIconHeight();
        double scale = Math.min((double) windowWidth / imgWidth, (double) windowHeight / imgHeight);
        int scaledWidth = (int) (imgWidth * scale);
        int scaledHeight = (int) (imgHeight * scale);

        // 缩放图片
        Image scaledImage = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void centerOnScreen() {
        setLocationRelativeTo(null); // 居中窗口
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