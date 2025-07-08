package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.event.FrameSelectedEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

@Component
@Slf4j
@Lazy
abstract public class CommonInternalJFrame extends JInternalFrame implements ComponentListener, InternalFrameListener, InitializingBean {
    protected final ResourceLoaderManager resources;
    protected final JDesktopPane desktopPane;

    protected CommonInternalJFrame(ResourceLoaderManager resources, JDesktopPane desktopPane) {
        super();
        this.resources = resources;
        this.desktopPane = desktopPane;
        this.addComponentListener(this);
        this.addInternalFrameListener(this);
        setResizable(true);
        setIconifiable(true);
        setMaximizable(true);
        setClosable(true);
    }

    protected abstract void initUI();

    @Override
    public void componentShown(ComponentEvent e) {
        Dimension parentSize = desktopPane.getSize();
        int width = parentSize.width - 100;
        int height = parentSize.height - 100;
        setSize(width, height);

        // 计算居中位置
        int x = (parentSize.width - width) / 2;
        int y = (parentSize.height - height) / 2;
        setLocation(x, y);
    }

    @Override
    public void componentResized(ComponentEvent e) {

    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {

    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame frame = e.getInternalFrame();
        FrameManager.publishEvent(new FrameSelectedEvent(frame, true));
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        FrameManager.publishEvent(new FrameSelectedEvent(e.getInternalFrame(), false));
    }

    public void afterPropertiesSet() {
        initUI();
    }
}
