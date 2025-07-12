package cn.ximuli.jframex.ui.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class WindowsEvent extends ApplicationEvent {

    public static final int WINDOW_DECORATIONS_CHANGED = 0;

    @Getter
    private int type;

    public WindowsEvent(int type, Object source) {
        super(source);
        this.type = type;
    }

    public static WindowsEvent createEvent (int type, Object data) {
        return new WindowsEvent(type, data);
    }
}
