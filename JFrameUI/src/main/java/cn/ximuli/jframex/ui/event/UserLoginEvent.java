package cn.ximuli.jframex.ui.event;

import cn.ximuli.jframex.model.LoggedInUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserLoginEvent extends ApplicationEvent {
    private LoggedInUser loggedInUser;
    public UserLoginEvent(LoggedInUser loggedInUser) {
        super(loggedInUser);
        this.loggedInUser = loggedInUser;
    }
}

