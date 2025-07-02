package cn.ximuli.jframex.ui;


import cn.ximuli.jframex.ui.test.AppSplashScreen2;
import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class ApplicationInitializer {


    public static void init(String... args) {
        initJFramePref(args);
        initTheme(args);
        initAppSplashScreen(args);
        initI18n(args);
    }

    public static void initJFramePref(String... args) {
        JFramePref.init(args);
    }

    public static void initAppSplashScreen (String... args) {
        AppSplashScreen2 appSplashScreen = AppSplashScreen2.getInstance();
        appSplashScreen.setVisible(true);
    }

    public static void initI18n(String... args) {
        System.setProperty("app.style.name", "default");
        //TODO
        System.setProperty("user.language","en");
    }

    public static void initTheme(String... args) {
        ThemeUIManager.setUp(args);
    }
}
