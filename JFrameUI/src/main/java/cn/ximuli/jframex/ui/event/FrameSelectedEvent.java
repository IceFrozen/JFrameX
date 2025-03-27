package cn.ximuli.jframex.ui.event;

import org.springframework.context.ApplicationEvent;

import javax.swing.*;

public class FrameSelectedEvent extends ApplicationEvent {

    private boolean selected = false;
    public FrameSelectedEvent(JInternalFrame source, boolean selected) {
        super(source);
        this.selected = selected;
    }

    public JInternalFrame getJInternalFrame() {
        return (JInternalFrame) this.source;
    }

    public boolean isSelected() {
        return this.selected;
    }
}
