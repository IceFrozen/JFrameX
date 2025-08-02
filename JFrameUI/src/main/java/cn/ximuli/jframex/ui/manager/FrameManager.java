package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.ui.AppSplashScreen;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.login.LoginFrame;
import cn.ximuli.jframex.common.constants.Status;
import cn.ximuli.jframex.ui.component.panels.StatePanel;
import cn.ximuli.jframex.ui.event.*;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j
@Component
public class FrameManager {
    private volatile Status status = Status.NONE;
    @Getter
    private volatile UISession uiSession;
    private final LoginFrame loginFrame;
    private final ResourceLoaderManager loaderManager;
    @Getter
    private volatile LoggedInUser currentUser;

    @Autowired
    public FrameManager(LoginFrame loginFrame, ResourceLoaderManager loaderManager) {
        this.loaderManager = loaderManager;
        this.loginFrame = loginFrame;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void initMainUI() {
    }

    @EventListener(ApplicationReadyEvent.class)
    public void handleApplication(ApplicationReadyEvent event) {
        log.info("springboot ready and begin loading resources");
        loaderManager.completeLoading();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void handleApplication(ApplicationStartedEvent event) {
        log.info("springboot starting: {}", event.getClass().getSimpleName());
        updateStatus(Status.LOADING);
        AppSplashScreen.setProgressBarValue(new ProgressEvent(10, "app starting..."));
    }

    @EventListener(ResourceReadyEvent.class)
    public void resourceLoadFinish(ResourceReadyEvent readyEvent) throws ClassNotFoundException {
        if (this.status == Status.LOADING) {
            AppSplashScreen.close();
            LoggedInUser user = JFramePref.getUser();
            if (user != null && !user.isExpired()) {
                userLogin(new UserLoginEvent(user, false));
            } else {
                loginFrame.setVisible(true);
                updateStatus(Status.SIGN_UP);
//                mainFrame.setVisible(true);
            }
        }
    }

    @EventListener(UserLoginEvent.class)
    public void userLogin(UserLoginEvent userLoginEvent) throws ClassNotFoundException {
        JFramePref.setUser(userLoginEvent.getLoggedInUser(), userLoginEvent.isRememberMe());
        updateStatus(Status.STARTING);
        this.currentUser = userLoginEvent.getLoggedInUser();
        this.uiSession = new UISession(userLoginEvent.getLoggedInUser(), loaderManager);
        this.uiSession.prepareUI();
        this.uiSession.getMainFrame().setVisible(true);
        updateStatus(Status.STARTED);
        loginFrame.reset();
        loginFrame.dispose();
    }

    @EventListener(UserLogoutEvent.class)
    public void userLogout(UserLogoutEvent userLogoutEvent) {
        JFramePref.reset();
        if (this.uiSession != null) {
            this.uiSession.getMainFrame().setVisible(false);
            updateStatus(Status.SIGN_UP);
            this.uiSession.destory();
            this.uiSession = null;
        }

        this.currentUser = null;
        loginFrame.reset();
        loginFrame.setVisible(true);
    }

    @EventListener(RestartEvent.class)
    public void restartUI(RestartEvent event) throws ClassNotFoundException {
        if (this.currentUser == null || this.currentUser.isExpired()) {
            userLogout(null);
            return;
        }

        if (this.uiSession != null) {
            this.uiSession.getMainFrame().setVisible(false);
            this.uiSession.destory();
            this.uiSession = new UISession(this.currentUser, loaderManager);
            this.uiSession.prepareUI();
            this.uiSession.getMainFrame().setVisible(true);
        }
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

        if (this.uiSession == null) {
            return;
        }
        MainFrame mainFrame = this.uiSession.getMainFrame();
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
        if (this.uiSession == null) {
            return null;
        }
        JInternalFrame iFrame = (JInternalFrame) this.uiSession.getInternalJFrame(clazz);
        try {
            if (!hasComponent(iFrame)) {
                this.uiSession.getDesktopPanel().add(iFrame);
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
        if (this.uiSession == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if (e.isSelected() && !this.uiSession.getStatePanel().isFrameSelected(e.getJInternalFrame())) {
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
        UISession currentUISession = getCurrentUISession();
        if (currentUISession == null) {
            return;
        }
        currentUISession.registerKeyAction(key, keyAction);
    }

    public synchronized void updateStatus(Status status) {
        this.status = status;
    }

    public synchronized void updateStatuePanel(JInternalFrame frame) {
        if (this.uiSession == null) {
            return;
        }
        this.uiSession.getStatePanel().frameSelected(frame);
    }

    public boolean hasComponent(JComponent com) {
        DesktopPanel desktopPanel = uiSession.getDesktopPanel();
        return Arrays.asList(desktopPanel.getComponents()).contains(com);
    }

    public static UISession getCurrentUISession() {
        return SpringUtils.getBean(FrameManager.class).getUiSession();
    }

    public static LoggedInUser getCurrentUser() {
        return SpringUtils.getBean(FrameManager.class).currentUser;
    }

}