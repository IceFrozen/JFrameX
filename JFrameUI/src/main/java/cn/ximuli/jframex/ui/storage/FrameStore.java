package cn.ximuli.jframex.ui.storage;

import cn.ximuli.jframex.model.User;

public class FrameStore {
    private static User loginedUser;
    public static synchronized void setUser(User user) {
        loginedUser = user;
    }

    public static User getUser() {
        return loginedUser;
    }

}
