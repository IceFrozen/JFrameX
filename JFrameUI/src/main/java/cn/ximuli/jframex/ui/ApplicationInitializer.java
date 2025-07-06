package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.ui.manager.AppSplashScreen;
import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.net.URL;


@Slf4j
public class ApplicationInitializer {

    public static void init(String... args) throws Exception {
        iconLoad();
        initJFramePref(args);
        initTheme(args);
        initI18n(args);
        initAppSplashScreen(args);
    }

    public static void initJFramePref(String... args) {
        JFramePref.init(args);
    }

    public static void initAppSplashScreen(String... args) {
        AppSplashScreen appSplashScreen = AppSplashScreen.getInstance();
        appSplashScreen.setVisible(true);
    }

    public static void initI18n(String... args) {
        System.setProperty(Application.APP_STYLE_NAME, Application.APP_STYLE_NAME_DEFAULT);
        System.setProperty(Application.APP_LANGUAGE, Application.APP_LANGUAGE_EN);
    }

    public static void initTheme(String... args) throws Exception {
        ThemeUIManager.setUp(args);
    }

    public static void iconLoad() {
        try {
            URL url = ApplicationInitializer.class.getResource(Application.APP_ICON);
            ImageIcon icon = new ImageIcon(ImageIO.read(url));
            if (SystemInfo.isWindows) {
                UIManager.put("Window.iconImage", icon);
            } else if (SystemInfo.isMacOS) {
                Class<?> applicationClass = Class.forName(Application.MAC.COM_APPLE_EAWT_APPLICATION);
                Object application = applicationClass.getMethod("getApplication").invoke(null);
                applicationClass.getMethod("setDockIconImage", Image.class)
                        .invoke(application, icon.getImage());
            }
        } catch (Exception e) {
            log.error("Error set Icon", e);
        }
    }
}
