package cn.ximuli.jframex.ui.test;

import cn.ximuli.jframex.common.utils.Formatter;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.panels.DesktopPanel;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

@Slf4j
public class AppSplashScreen2 extends JWindow {
    private static AppSplashScreen2 INSTANCE = new AppSplashScreen2();
    private JProgressBar progressBar;
    private JTabbedPane tabPlacementTabbedPane = new JTabbedPane();
    private JToggleButton scrollButton = new JToggleButton();

    public AppSplashScreen2() {
        ImageIcon icon = loadSplashImage();

        JLabel splashLabel = new JLabel(icon);
        initTabPlacementTabs( tabPlacementTabbedPane, List.of(splashLabel));
        tabPlacementTabbedPane.setLayout(new BorderLayout());
        tabPlacementTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        //splashLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 0));
        splashLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        setLayout(new BorderLayout());
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); // 移除内边距
        customizeTabAppearance();
        jPanel.add(tabPlacementTabbedPane, BorderLayout.CENTER);
        add(jPanel, BorderLayout.CENTER);
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
    public static AppSplashScreen2 getInstance() {
        return INSTANCE;
    }


    private void addTab( JTabbedPane tabbedPane, String title, JLabel label ) {
        tabbedPane.addTab( title, createTab( label ) );
    }

    private JComponent createTab( JLabel label ) {
        label.setHorizontalAlignment( SwingConstants.CENTER );
        JPanel tab = new JPanel( new BorderLayout() );
        tab.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        tab.add( label, BorderLayout.CENTER );
        return tab;
    }

    private void initTabPlacementTabs(JTabbedPane tabbedPane , List<JLabel> labels) {
        for (JLabel label : labels) {
            addTab( tabbedPane, null, label );
        }
        tabbedPane.setTabPlacement( JTabbedPane.BOTTOM );
    }

    private void customizeTabAppearance() {
        // 隐藏标签并移除间距
        tabPlacementTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT); // 强制单标签布局
        tabPlacementTabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
                return 1; // 设置标签高度为 0
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                // 不绘制标签边框
            }
        });
        tabPlacementTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
    }
}