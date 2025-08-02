package cn.ximuli.jframex.ui.event;

import org.springframework.context.ApplicationEvent;
import javax.swing.*;

public class CreateFrameEvent<T extends JInternalFrame> extends ApplicationEvent {

    public Object[] args;

    public CreateFrameEvent(Class<T> aClass, Object... args) {
        super(aClass);
        this.args = args;
    }

    public Object[] getArgs() {
        return this.args;
    }
}
