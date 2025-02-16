package cn.ximuli.jframex.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MenuBar extends JMenuBar {

    JDesktopPane desktopPanel;


    @Autowired
    public MenuBar(JDesktopPane desktopPanel) {
        super();
        this.desktopPanel = desktopPanel;
        initialize();
    }

    private void initialize() {
        this.setSize(new Dimension(600, 24));
    }
}
