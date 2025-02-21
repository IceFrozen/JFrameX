package cn.ximuli.jframex.ui.event;

import cn.ximuli.jframex.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
public class UserLoginEvent extends ApplicationEvent {
    private User user;
    public UserLoginEvent(User user) {
        super(user);
        this.user = user;
    }
}

