package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.ui.component.DesktopPanel;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.common.constants.Status;
import cn.ximuli.jframex.ui.component.StatePanel;
import cn.ximuli.jframex.ui.event.*;
import cn.ximuli.jframex.ui.login.LoginDialog;
import cn.ximuli.jframex.ui.storage.FrameStore;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Component
public class FrameManager {
    public static final String KEY_SYSTEM_SCALE_FACTOR = "systemScaleFactor";
    private volatile Status status = Status.NONE;
    private final MainFrame mainFrame;
    private final LoginDialog loginDialog;
    private final ResourceLoaderManager loaderManager;
    private final StatePanel statePanel;
    private final DesktopPanel desktopPanel;

    @Autowired
    public FrameManager(MainFrame mainFrame, LoginDialog loginDialog, ResourceLoaderManager loaderManager, StatePanel statePanel, DesktopPanel desktopPanel) {
        this.mainFrame = mainFrame;
        this.loginDialog = loginDialog;
        this.loaderManager = loaderManager;
        this.statePanel = statePanel;
        this.desktopPanel = desktopPanel;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initMainUI() {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void handleApplication(ApplicationReadyEvent event) {
        log.info("springboot ready and begin loading resources");
        registerSystemScaleFactors(event.getApplicationContext().getBean(MainFrame.class));
        loaderManager.completeLoading();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void handleApplication(ApplicationStartedEvent event) {
        log.info("springboot starting: {}", event.getClass().getSimpleName());
        updateStatus(Status.LOADING);
        initSystemScale();
        AppSplashScreen.setProgressBarValue(new ProgressEvent(10, "app starting..."));
    }

    @EventListener(ResourceReadyEvent.class)
    public void resourceLoadFinish(ResourceReadyEvent readyEvent) {
        if (this.status == Status.LOADING) {
            boolean closeSuccess = AppSplashScreen.close();
            userLogin(new UserLoginEvent(new LoggedInUser("username", "password")));
            // TODO 这里判断直接登录
            if (closeSuccess) {
                // loginDialog.initialize();
                // loginDialog.setVisible(true);
                // updateStatus(Status.SIGN_UP);
            }
        }
    }

    @EventListener(UserLoginEvent.class)
    public void userLogin(UserLoginEvent userLoginEvent) {
        FrameStore.setUser(userLoginEvent.getLoggedInUser());
        loginDialog.setVisible(false);
        mainFrame.setVisible(true);
        updateStatus(Status.STARTED);

    }

    @EventListener(MenuButtonClickEvent.class)
    public void menuButtonClick(MenuButtonClickEvent event) {
        JMenuItem jMenuItem = event.getJMenuItem();
        Object aClass = jMenuItem.getClientProperty("class");
        createIFrame((Class<?>) aClass);
    }


    @EventListener(CreateFrameEvent.class)
    public void createFrameEvent(CreateFrameEvent<? extends JInternalFrame> event) {
        Class<?> clazz = (Class<?>) event.getSource();
        createIFrame(clazz, event.getArgs());
    }

    @EventListener(WindowsEvent.class)
    public void handleWindowEvent(WindowsEvent event) {
        log.info("event: {}", event);
        int type = event.getType();
        if (type == WindowsEvent.WINDOW_DECORATIONS_CHANGED) {
            boolean windowDecorations = (boolean) event.getSource();
            if (SystemInfo.isLinux) {
                // enable/disable custom window decorations
                JFrame.setDefaultLookAndFeelDecorated(windowDecorations);
                JDialog.setDefaultLookAndFeelDecorated(windowDecorations);

                // dispose frame, update decoration and re-show frame
                mainFrame.dispose();
                mainFrame.setUndecorated(windowDecorations);
                mainFrame.getRootPane().setWindowDecorationStyle(windowDecorations ? JRootPane.FRAME : JRootPane.NONE);
                mainFrame.setVisible(true);
            } else {
                // change window decoration of all frames and dialogs
                FlatLaf.setUseNativeWindowDecorations(windowDecorations);
            }
        }
    }



    public JInternalFrame createIFrame(Class<?> clazz, Object... args) {
        JInternalFrame iFrame = (JInternalFrame) SpringUtils.getBean(clazz, args);
        try {
            if (!hasComponent(iFrame)) {
                desktopPanel.add(iFrame);
            }
            if (iFrame.isVisible()) {
                iFrame.setSelected(true);
                return iFrame;
            }
            //TODO center
            int frameH = iFrame.getPreferredSize().height;
            int panelH = iFrame.getContentPane().getPreferredSize().height;
            int fSpacing = frameH - panelH;
            int nextFrameX = fSpacing;
            int nextFrameY = fSpacing;
            iFrame.setLocation(nextFrameX, nextFrameY);
//            iFrame.setResizable(true);
//            iFrame.setMaximizable(false);
            iFrame.setVisible(true);
            iFrame.setSelected(true);
        } catch (Exception e) {
            //TODO op
            e.printStackTrace();
        }
        return iFrame;
    }

    @EventListener
    public void JInternalFrameSelected(FrameSelectedEvent e) {
        SwingUtilities.invokeLater(() -> {
            String statusPanelContext = I18nHelper.getMessage("app.mainframe.state.selected");
            if (e.isSelected() && !statePanel.isFrameSelected(e.getJInternalFrame())) {
                updateStatuePanel(e.getJInternalFrame());
            } else {
                updateStatuePanel(null);
            }
        });
    }

    public static void publishEvent(ApplicationEvent event) {
        SpringUtils.applicationContext.publishEvent(event);
    }


    public static void showMessageJOptionPane(java.awt.Component parentComponent, String message, ActionEvent e) {
       showJOptionPane(parentComponent, message, JOptionPane.PLAIN_MESSAGE, e);
    }


    public static void showJOptionPane(java.awt.Component parentComponent, String message, int type, ActionEvent e) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(parentComponent, e.getActionCommand(), message, type));
    }


    public static void registerKeyAction(int key, BiConsumer<MainFrame, ActionEvent> keyAction) {
        MainFrame main = SpringUtils.getBean(MainFrame.class);
        ((JComponent) main.getContentPane()).registerKeyboardAction(
                e -> keyAction.accept(main, e),
                KeyStroke.getKeyStroke(key, 0, false),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    public static void initSystemScale() {
        if (System.getProperty("sun.java2d.uiScale") == null) {
            String scaleFactor = JFramePref.state.get(KEY_SYSTEM_SCALE_FACTOR, null);
            if (scaleFactor != null) {
                System.setProperty("sun.java2d.uiScale", scaleFactor);
                log.info("JFrameX: setting 'sun.java2d.uiScale' to {}", scaleFactor);
                log.info("use 'Alt+Shift+F1...12' to change it to 1x...4x");
            }
        }
    }

    public static void registerSystemScaleFactors(JFrame frame) {
        registerSystemScaleFactor(frame, "alt shift F1", null);
        registerSystemScaleFactor(frame, "alt shift F2", "1");

        if (SystemInfo.isWindows) {
            registerSystemScaleFactor(frame, "alt shift F3", "1.25");
            registerSystemScaleFactor(frame, "alt shift F4", "1.5");
            registerSystemScaleFactor(frame, "alt shift F5", "1.75");
            registerSystemScaleFactor(frame, "alt shift F6", "2");
            registerSystemScaleFactor(frame, "alt shift F7", "2.25");
            registerSystemScaleFactor(frame, "alt shift F8", "2.5");
            registerSystemScaleFactor(frame, "alt shift F9", "2.75");
            registerSystemScaleFactor(frame, "alt shift F10", "3");
            registerSystemScaleFactor(frame, "alt shift F11", "3.5");
            registerSystemScaleFactor(frame, "alt shift F12", "4");
        } else {
            // Java on macOS and Linux supports only integer scale factors
            registerSystemScaleFactor(frame, "alt shift F3", "2");
            registerSystemScaleFactor(frame, "alt shift F4", "3");
            registerSystemScaleFactor(frame, "alt shift F5", "4");
        }
    }

    private static void registerSystemScaleFactor(JFrame frame, String keyStrokeStr, String scaleFactor) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeStr);
        if (keyStroke == null)
            throw new IllegalArgumentException("Invalid key stroke '" + keyStrokeStr + "'");

        ((JComponent) frame.getContentPane()).registerKeyboardAction(
                e -> applySystemScaleFactor(frame, scaleFactor),
                keyStroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static void applySystemScaleFactor(JFrame frame, String scaleFactor) {
        //TODO i8
        if (JOptionPane.showConfirmDialog(frame,
                "Change system scale factor to "
                        + (scaleFactor != null ? scaleFactor : "default")
                        + " and exit?",
                frame.getTitle(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
            return;

        if (scaleFactor != null)
            JFramePref.state.put(KEY_SYSTEM_SCALE_FACTOR, scaleFactor);
        else
            JFramePref.state.remove(KEY_SYSTEM_SCALE_FACTOR);

        System.exit(0);
    }


    public synchronized void updateStatus(Status status) {
        this.status = status;
    }

    public synchronized void updateStatuePanel(JInternalFrame frame) {
        statePanel.frameSelected(frame);
    }

    public boolean hasComponent(JComponent com) {
        return Arrays.asList(desktopPanel.getComponents()).contains(com);
    }
}