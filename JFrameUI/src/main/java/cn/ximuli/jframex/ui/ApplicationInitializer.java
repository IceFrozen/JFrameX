package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.ui.manager.AppSplashScreen;
import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;


@Slf4j
public class ApplicationInitializer {


    public static void init(String... args) {
        initJFramePref(args);
        initTheme(args);
        initI18n(args);
        initAppSplashScreen(args);

    }

    public static void initJFramePref(String... args) {
        JFramePref.init(args);
    }

    public static void initAppSplashScreen (String... args) {
        AppSplashScreen appSplashScreen = AppSplashScreen.getInstance();
        appSplashScreen.setVisible(true);
    }

    public static void initI18n(String... args) {
        System.setProperty("app.style.name", "default");
        System.setProperty("user.language","en");
    }

    public static void initTheme(String... args) {
        ThemeUIManager.setUp(args);
    }
}
