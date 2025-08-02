package cn.ximuli.jframex.ui.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class RestartEvent  extends ApplicationEvent {
    public RestartEvent(Object source) {
        super(source);
    }

    public RestartEvent(Object source, Clock clock) {
        super(source, clock);
    }
}
