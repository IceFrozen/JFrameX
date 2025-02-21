package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.Status;
import cn.ximuli.jframex.ui.event.ProgressEvent;
import cn.ximuli.jframex.ui.event.ResourceReadyEvent;
import cn.ximuli.jframex.ui.event.UserLoginEvent;
import cn.ximuli.jframex.ui.login.LoginDialog;
import cn.ximuli.jframex.ui.storage.FrameStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class FrameManager implements ApplicationContextAware {
    private volatile Status status = Status.NONE;

    @Autowired
    private MainFrame mainFrame;
    @Autowired
    private LoginDialog loginDialog;
    @Autowired
    private ResourceLoaderManager loaderManager;

    private static ApplicationContext context;

    @EventListener(ContextRefreshedEvent.class)
    public void initMainUI() {
        // 主界面初始化完成后自动隐藏闪屏
    }

    @EventListener(SpringApplicationEvent.class)
    public void handleApplication(SpringApplicationEvent event) {
        log.info("springboot starting: {}", event.getClass().getSimpleName());
        if (event.getClass().isAssignableFrom(ApplicationReadyEvent.class)) {
            loaderManager.loading();
            return;
        }

        loadResource(new ProgressEvent(10, "spring init"));
    }

    @EventListener(ProgressEvent.class)
    public void loadResource(ProgressEvent event) {
        AppSplashScreen.setProgressBarValue(event);
    }

    @EventListener(ResourceReadyEvent.class)
    public void resourceLoadFinish(ResourceReadyEvent readyEvent) {
        if (this.status == Status.LOADING) {
            boolean closeSuccess = AppSplashScreen.close();
//            mainFrame.setVisible(true);
            // TODO 这里判断直接登录
            if (closeSuccess) {
                loginDialog.initialize();
                loginDialog.setVisible(true);

            }
            updateStatus(Status.STARTED);
        }
    }
    @EventListener(UserLoginEvent.class)
    public void userLogin(UserLoginEvent userLoginEvent) {
        FrameStore.setUser(userLoginEvent.getUser());
        loginDialog.setVisible(false);
        mainFrame.setVisible(true);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        updateStatus(Status.LOADING);
    }

    public static void publishEvent(ApplicationEvent event) {
        context.publishEvent(event);
    }

    public synchronized void updateStatus(Status status) {
        this.status = status;
    }
}