package cn.ximuli.jframex.ui.theme;

import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.demo.DemoFrame;
import cn.ximuli.jframex.ui.demo.DemoPrefs;
import cn.ximuli.jframex.ui.demo.FlatLafDemo;
import cn.ximuli.jframex.ui.manager.AppSplashScreen;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

@Slf4j
public class ThemeUIManager {
    public static final String KEY_LAF_CLASS_NAME = "lafClassName";
    public static final String KEY_LAF_THEME_FILE = "lafThemeFile";
    public static final String KEY_SYSTEM_SCALE_FACTOR = "systemScaleFactor";
    public static final String KEY_LAF_SYNC = "lafSync";
    static boolean screenshotsMode = Boolean.parseBoolean(System.getProperty("jframex.screenshotsMode"));

    public static void setUp(String... args) {
        init();
        setupLaf(args);
    }


    private static void init() {
        // Linux
        if (SystemInfo.isLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        if (screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty("flatlaf.uiScale") == null) {
            System.setProperty("flatlaf.uiScale", "2x");
        }
        initSystemScale();
        initFont();
    }

    public static void setupLaf(String[] args) {
        //IJThemesDump.install();
        // set look and feel
        try {
            if (args.length > 0) {
                UIManager.setLookAndFeel(args[0]);
            } else {
                restoreLaf();
            }
        } catch (Throwable ex) {
            log.error("Init theme error", ex);
            FlatLightLaf.setup();
        }

        // remember active look and feel
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                log.info("lookAndFeel changed: {}", e.getNewValue());
                JFramePref.state.put(KEY_LAF_CLASS_NAME, UIManager.getLookAndFeel().getClass().getName());
            }
        });
    }

    private static void restoreLaf() throws Exception {
        String lafClassName = JFramePref.state.get(KEY_LAF_CLASS_NAME, FlatIntelliJLaf.class.getName());
        if (FlatPropertiesLaf.class.getName().equals(lafClassName) ||
                IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
            String themeFileName = JFramePref.state.get(KEY_LAF_THEME_FILE, "");
            File file = ResourceUtils.getFile("FlatLightLaf.properties");

            if (!themeFileName.isEmpty()) {
                File themeFile = new File(themeFileName);
                if (themeFileName.endsWith(".properties")) {
                    String themeName = StringUtils.removeTrailing(themeFile.getName(), ".properties");
                    FlatLaf.setup(new FlatPropertiesLaf(themeName, themeFile));
                } else {
                    FlatLaf.setup(IntelliJTheme.createLaf(new FileInputStream(themeFile)));
                }
            } else {
                FlatLightLaf.setup();
            }
        } else {
            //UIManager.setLookAndFeel(lafClassName);
            UIManager.setLookAndFeel(new FlatLightLaf());
        }
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

    private static void initFont() {

        // install fonts for lazy loading
        FlatInterFont.install();
//        FlatInterFont.installLazy();
//            FlatJetBrainsMonoFont.installLazy();
//            FlatRobotoFont.installLazy();
//            FlatRobotoMonoFont.installLazy();
        // use Inter font by default
//			FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
//			FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
//			FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
        // use Roboto font by default
//			FlatLaf.setPreferredFontFamily( FlatRobotoFont.FAMILY );
//			FlatLaf.setPreferredLightFontFamily( FlatRobotoFont.FAMILY_LIGHT );
//			FlatLaf.setPreferredSemiboldFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );
        // use JetBrains Mono font
//			FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );
        // use Rboto Mono font
//			FlatLaf.setPreferredMonospacedFontFamily( FlatRobotoMonoFont.FAMILY );
        // install own repaint manager to fix repaint issues at 125%, 175%, 225%, ... on Windows
//			HiDPIUtils.installHiDPIRepaintManager();
        // application specific UI defaults
        String packageName = Application.class.getPackageName();
        log.info("package:{}", packageName);
        FlatLaf.registerCustomDefaultsSource("style");
        // install inspectors
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");

    }
}
