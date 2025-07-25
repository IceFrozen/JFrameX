package cn.ximuli.jframex.ui.storage;

import cn.ximuli.jframex.common.utils.JSONUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;

@Slf4j
public class JFramePref {
    private static volatile LoggedInUser loggedInUser;
    private static final int MAX_USER_LIST_SIZE = 10;

    private static final String JFRAMEX_STATE_ROOT_PATH = "/JFrameX";

    private static final String USER_KEY = "user";
    private static final String TOKEN_LIST = "user_list";
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



    public static synchronized void logOut() {
        JFramePref.loggedInUser = null;
        state.remove(USER_KEY);
    }

    
    public static synchronized void setUser(LoggedInUser loggedInUser, boolean rememberMe) {
        JFramePref.loggedInUser = loggedInUser;
        String userJsonStr = JSONUtil.to(loggedInUser);
        if (rememberMe) {
            state.put(USER_KEY, userJsonStr);
            String userListStr = state.get(TOKEN_LIST, "[]");
            List<String> userList = JSONUtil.fromList(userListStr, String.class);
            String userName = loggedInUser.getUsername(); // Assume username as identifier
            if (userList.contains(userName)) {
                userList.remove(userName);
            }
            userList.add(0, userName); // Add to first position
            // Limit size to 10
            while (userList.size() > MAX_USER_LIST_SIZE) {
                userList.remove(userList.size() - 1);
            }
            state.put(TOKEN_LIST, JSONUtil.to(userList));
        }
    }

    public static void reset() {
        JFramePref.loggedInUser = null;
        state.remove(USER_KEY);
    }

    public static LoggedInUser getUser() {
        return loggedInUser;
    }

    public static List<String> getHistoryUserList() {
        String userListStr = state.get(TOKEN_LIST, "[]");
        List<String> userList = JSONUtil.fromList(userListStr, String.class);
        return userList;
    }
}
