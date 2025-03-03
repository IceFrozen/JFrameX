package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.model.User;
import cn.ximuli.jframex.ui.component.DesktopPanel;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.common.constants.Status;
import cn.ximuli.jframex.ui.component.StatePanel;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import cn.ximuli.jframex.ui.event.UserLoginEvent;
import cn.ximuli.jframex.ui.login.LoginDialog;
import cn.ximuli.jframex.ui.storage.FrameStore;
import cn.ximuli.jframex.ui.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

@Slf4j
@Component
public class FrameManager {
    private static ApplicationContext context;

    private volatile Status status = Status.NONE;
    private final MainFrame mainFrame;
    private final LoginDialog loginDialog;
    private final ResourceLoaderManager loaderManager;
    private final StatePanel statePanel;
    private final DesktopPanel desktopPanel;
    private int nextFrameX;
    private int nextFrameY;

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
        // TODO
    }

    @EventListener(ApplicationReadyEvent.class)
    public void handleApplication(ApplicationReadyEvent event) {
        log.info("springboot ready and begin loading resources");
        loaderManager.loading();
    }

    @EventListener(ApplicationStartedEvent.class)
    public void handleApplication(ApplicationStartedEvent event) {
        log.info("springboot starting: {}", event.getClass().getSimpleName());
        updateStatus(Status.LOADING);
        AppSplashScreen.setProgressBarValue(new ProgressEvent(10, "app starting..."));
    }

    @EventListener(ResourceReadyEvent.class)
    public void resourceLoadFinish(ResourceReadyEvent readyEvent) {
        if (this.status == Status.LOADING) {
            boolean closeSuccess = AppSplashScreen.close();
            userLogin(new UserLoginEvent(new User("username", "password")));
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
        FrameStore.setUser(userLoginEvent.getUser());
        loginDialog.setVisible(false);
        mainFrame.setVisible(true);
        updateStatus(Status.STARTED);

    }

    @EventListener(MenuButtonClickEvent.class)
    public void menuButtonClick(MenuButtonClickEvent event) {
        JMenuItem jMenuItem = event.getJMenuItem();
        Object aClass = jMenuItem.getClientProperty("class");
        createIFrame(jMenuItem, (Class<?>) aClass);
    }

    public JInternalFrame createIFrame(JMenuItem item, Class<?> clazz) {
        JInternalFrame iFrame = (JInternalFrame) SpringUtils.getBean(clazz);

        try {
            iFrame.setFrameIcon(item.getIcon());
            iFrame.setLocation(nextFrameX, nextFrameY);
            int frameH = iFrame.getPreferredSize().height;
            int panelH = iFrame.getContentPane().getPreferredSize().height;
            int fSpacing = frameH - panelH;
            nextFrameX += fSpacing;
            nextFrameY += fSpacing;
            if (nextFrameX + iFrame.getWidth() > desktopPanel.getWidth())
                nextFrameX = 0;
            if (nextFrameY + iFrame.getHeight() > desktopPanel.getHeight())
                nextFrameY = 0;
            desktopPanel.add(iFrame);
            iFrame.setResizable(true);
            iFrame.setMaximizable(false);
            iFrame.setVisible(true);

            iFrame.setSelected(true);
            updateStatuePanel(iFrame.getTitle());
            iFrame.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameActivated(InternalFrameEvent e) {
                    super.internalFrameActivated(e);
                    JInternalFrame frame = e.getInternalFrame();
                    updateStatuePanel(frame.getTitle());
                }
                public void internalFrameDeactivated(InternalFrameEvent e) {
                    updateStatuePanel(I18nHelper.getMessage("app.mainframe.state.selected"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iFrame;
    }


    public static void publishEvent(ApplicationEvent event) {
        SpringUtils.applicationContext.publishEvent(event);
    }

    public synchronized void updateStatus(Status status) {
        this.status = status;
    }

    public synchronized void updateStatuePanel(String info) {
        SwingUtilities.invokeLater(() -> statePanel.setStateLabelText(info));
    }
}