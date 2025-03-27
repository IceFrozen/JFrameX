package cn.ximuli.jframex.ui.storage;

import cn.ximuli.jframex.model.LoggedInUser;

public class FrameStore {
    private static LoggedInUser loggedInUser;
    public static synchronized void setUser(LoggedInUser loggedInUser) {
        FrameStore.loggedInUser = loggedInUser;
    }

    public static LoggedInUser getUser() {
        return loggedInUser;
    }

}
