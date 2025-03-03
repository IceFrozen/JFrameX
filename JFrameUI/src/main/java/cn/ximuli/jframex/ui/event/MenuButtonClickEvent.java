package cn.ximuli.jframex.ui.event;

import org.springframework.context.ApplicationEvent;

import javax.swing.*;

public class MenuButtonClickEvent  extends ApplicationEvent {
    public MenuButtonClickEvent(JMenuItem source) {
        super(source);
    }

    public JMenuItem getJMenuItem() {
        return (JMenuItem) this.source;
    }
}

