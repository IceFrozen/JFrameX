package cn.ximuli.jframex.ui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class UserLogoutEvent extends ApplicationEvent {


    public UserLogoutEvent(Object source) {
        super(source);
    }

    public UserLogoutEvent(Object source, Clock clock) {
        super(source, clock);
    }
}

