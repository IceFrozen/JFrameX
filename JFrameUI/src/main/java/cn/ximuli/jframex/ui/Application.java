package cn.ximuli.jframex.ui;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.component.SettingInternalJFrame;
import cn.ximuli.jframex.ui.event.CreateFrameEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.Environment;


@SpringBootApplication(scanBasePackages = {"cn.ximuli.jframex"})
@Slf4j
public class Application implements CommandLineRunner {
    public static final String APP_STYLE_NAME = "app.style.name";
    public static final String APP_STYLE_NAME_DEFAULT = "default";
    public static final String APP_LANGUAGE = "app.language";
    public static final String APP_LANGUAGE_EN = "en";
    public static final String APP_ICON = "/style/icon.png";

    public static void main(String[] args) throws Exception {
        ApplicationInitializer.init(args);
        new SpringApplicationBuilder(Application.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        FrameManager.publishEvent(new CreateFrameEvent<>(SettingInternalJFrame.class));

    }



    public static class MAC {
        public static final String COM_APPLE_EAWT_APPLICATION = "com.apple.eawt.Application";
        public static final String COM_APPLE_MRJ_APPLICATION_APPLE_MENU_ABOUT_NAME= "com.apple.mrj.application.apple.menu.about.name";
        public static final String APPLE_AWT_FULL_WINDOW_CONTENT = "apple.awt.fullWindowContent";
        public static final String APPLE_AWT_TRANSPARENT_TITLE_BAR = "apple.awt.transparentTitleBar";
        public static final String APPLE_AWT_WINDOW_TITLE_VISIBLE = "apple.awt.windowTitleVisible";


        public static final String APPLE_AWT_FULL_FULL_SCREENABLE = "apple.awt.fullscreenable";
        public static final String APPLE_AWT_WINDOW_BRUSH_IMAGES = "apple.awt.windowBrushImages";
        public static final String APPLE_AWT_WINDOW_TITLE_HIDDEN = "apple.awt.windowTitleHidden";
        public static final String APPLE_AWT_WINDOW_TITLE_HIDE_ON_CLOSE = "apple.awt.windowTitleHideOnClose";
        public static final String APPLE_AWT_WINDOW_TITLE_TEXT_COLOR = "apple.awt.windowTitleTextColor";
        public static final String APPLE_AWT_WINDOW_TITLE_TEXT_SHADOW_COLOR = "apple.awt.windowTitleTextShadowColor";
        public static final String APPLE_AWT_WINDOW_TITLE_VISIBLE_ON_MAXIMIZED = "apple.awt.windowTitleVisibleOnMaximized";

        public static final String APPLE_AWT_WINDOW_TITLE_HAS_TRANSPARENT_BACKGROUND = "apple.awt.windowTitleHasTransparentBackground";
        public static final String APPLE_AWT_WINDOW_TITLE_TEXT_SHADOW_OFFSET = "apple.awt.windowTitleTextShadowOffset";
        public static final String APPLE_AWT_WINDOW_TITLE_TEXT_SHADOW_SIZE = "apple.awt.windowTitleTextShadowSize";


    }

}
