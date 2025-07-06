package cn.ximuli.jframex.ui.storage;

import cn.ximuli.jframex.ui.component.DesktopPanel;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.prefs.Preferences;

@Slf4j
public class JFramePref {
    private static final String JFRAMEX_STATE_ROOT_PATH = "/JFrameX";
    public static final Preferences state = Preferences.userRoot().node(JFRAMEX_STATE_ROOT_PATH);

    private static final String SPRING_APPLICATION_PROPERTIES = "application.properties";
    private static final String STYLE_PROPERTIES = "style.properties";
    private static final String MAC_STYLE_PROPERTIES_PATH = "device/mac/";
    private static final String WINDOWS_STYLE_PROPERTIES_PATH = "device/windows/";
    private static final String LINUX_STYLE_PROPERTIES_PATH = "device/linux/";


    public static void init(String... args) {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(SPRING_APPLICATION_PROPERTIES);
            System.getProperties().putAll(properties);
            Properties devProperties = null;
            if (SystemInfo.isMacOS) {
                devProperties = PropertiesLoaderUtils.loadAllProperties(MAC_STYLE_PROPERTIES_PATH + STYLE_PROPERTIES);
            } else if (SystemInfo.isLinux) {
                devProperties = PropertiesLoaderUtils.loadAllProperties(LINUX_STYLE_PROPERTIES_PATH + STYLE_PROPERTIES);
            } else if (SystemInfo.isWindows) {
                devProperties = PropertiesLoaderUtils.loadAllProperties(WINDOWS_STYLE_PROPERTIES_PATH + STYLE_PROPERTIES);
            }

            if (devProperties != null) {
                System.getProperties().putAll(devProperties);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
