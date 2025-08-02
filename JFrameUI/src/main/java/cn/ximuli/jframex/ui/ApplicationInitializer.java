package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.manager.ThemeUIManager;
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

    private static void initJFramePref(String... args) {
        JFramePref.init(args);
    }

    private static void initAppSplashScreen(String... args) {
        AppSplashScreen appSplashScreen = AppSplashScreen.getInstance();
        appSplashScreen.setVisible(true);
    }

    private static void initI18n(String... args) throws Exception {
        System.setProperty(Application.APP_STYLE_NAME, Application.APP_STYLE_NAME_DEFAULT);
        String currentLanguage = JFramePref.state.get(Application.APP_LANGUAGE, null);
        if (currentLanguage == null) {
            JFramePref.state.put(Application.APP_LANGUAGE, Application.APP_LANGUAGE_EN);
        }
        System.setProperty(Application.APP_LANGUAGE, JFramePref.state.get(Application.APP_LANGUAGE, Application.APP_LANGUAGE_EN));
        I18nHelper.init();

    }

    private static void initTheme(String... args) {
        ThemeUIManager.setUp(args);
    }

    private static void iconLoad() {
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
