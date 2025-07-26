package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.component.themes.IJThemeInfo;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.StringUtils;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class ThemeUIManager {
    public static final String KEY_LAF_CLASS_NAME = "lafClassName";
    public static final String KEY_LAF_THEME_FILE = "lafThemeFile";
    public static final String KEY_LAF_SYNC = "lafSync";
    static boolean screenshotsMode = Boolean.parseBoolean(System.getProperty("jframex.screenshotsMode"));
    private static String[] availableFontFamilyNames;
    public static void setUp(String... args) throws Exception {
        init();
        setupLaf(args);
    }

    private static void init() throws Exception {
        // Linux
        if (SystemInfo.isLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        if (screenshotsMode && !SystemInfo.isJava_9_orLater && System.getProperty("flatlaf.uiScale") == null) {
            System.setProperty("flatlaf.uiScale", "2x");
        }
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
        String lafClassName = JFramePref.state.get(KEY_LAF_CLASS_NAME, FlatDarkLaf.class.getName());
        if (FlatPropertiesLaf.class.getName().equals(lafClassName) ||
                IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
            String themeFileName = JFramePref.state.get(KEY_LAF_THEME_FILE, "");
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
            UIManager.setLookAndFeel(lafClassName);
        }
    }


    private static void initFont() {
        // install fonts for lazy loading
        FlatInterFont.install();
        availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
        Arrays.sort(availableFontFamilyNames);
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

    public static boolean isDarkMode() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        if (lookAndFeel instanceof FlatLaf f) {
            return f.isDark();
        }
        return false;
    }

    public static void themeChangeListener(Runnable runnable) {
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                EventQueue.invokeLater(runnable);
            }
        });
    }

    public static void lookAndFeelChanged(String lafClassName) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            // change look and feel
            try {
                UIManager.setLookAndFeel(lafClassName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // clear custom default font when switching to non-FlatLaf LaF
            if (!(UIManager.getLookAndFeel() instanceof FlatLaf))
                UIManager.put("defaultFont", null);
            // update all components
            FlatLaf.updateUI();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();

        });
    }

    public static void setTheme(IJThemeInfo themeInfo, boolean reload) {
        if (themeInfo == null) {
            return;
        }
        // change look and feel
        if (themeInfo.getLafClassName() != null) {
            if (!reload && themeInfo.getLafClassName().equals(UIManager.getLookAndFeel().getClass().getName())) {
                return;
            }
            if (!reload) {
                FlatAnimatedLafChange.showSnapshot();
            }
            try {
                log.info("change theme: {}", themeInfo);
                UIManager.setLookAndFeel(themeInfo.getLafClassName());
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create '" + themeInfo.getThemeFile() + "'.", ex);

            }
        } else if (themeInfo.getThemeFile() != null) {
            if (!reload)
                FlatAnimatedLafChange.showSnapshot();
            try {
                if (themeInfo.getThemeFile().getName().endsWith(".properties")) {
                    FlatLaf.setup(new FlatPropertiesLaf(themeInfo.getName(), themeInfo.getThemeFile()));
                } else {
                    FlatLaf.setup(IntelliJTheme.createLaf(new FileInputStream(themeInfo.getThemeFile())));
                }

                JFramePref.state.put(ThemeUIManager.KEY_LAF_THEME_FILE, themeInfo.getThemeFile().getAbsolutePath());
            } catch (Exception ex) {
                LoggingFacade.INSTANCE.logSevere(null, ex);
                throw new RuntimeException("Failed to load '" + themeInfo.getThemeFile() + "'.", ex);
            }
        } else {
            throw new RuntimeException("Missing lafClassName for '" + themeInfo.getThemeFile() + "'.");
        }

        // update all components
        FlatLaf.updateUI();
        if (!reload) {
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        }

    }

    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = e.getActionCommand();

        FlatAnimatedLafChange.showSnapshot();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = FontUtils.getCompositeFont(fontFamily, font.getStyle(), font.getSize());
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void fontSizeChanged(ActionEvent e) {
        String fontSizeStr = e.getActionCommand();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Integer.parseInt(fontSizeStr));
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
    }

    private void restoreFont() {
        UIManager.put("defaultFont", null);
        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void incrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) (font.getSize() + 1));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void decrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Math.max(font.getSize() - 1, 10));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    void updateFontMenuItems() {
        // get current font
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());

        // add font families
        ArrayList<String> families = new ArrayList<>(Arrays.asList(
                "Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans",
                "Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto",
                "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        if (!families.contains(currentFamily))
            families.add(currentFamily);
        families.sort(String.CASE_INSENSITIVE_ORDER);
        // add font
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList(
                "10", "11", "12", "14", "16", "18", "20", "24", "28"));
        if (!sizes.contains(currentSize))
            sizes.add(currentSize);
        sizes.sort(String.CASE_INSENSITIVE_ORDER);

    }


    public static void UIScaleChangeListener(Runnable runnable) {
        UIManager.addPropertyChangeListener(e -> EventQueue.invokeLater(runnable));
    }
}
