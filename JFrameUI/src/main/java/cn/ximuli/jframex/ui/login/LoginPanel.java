package cn.ximuli.jframex.ui.login;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class LoginPanel extends JPanel {
    private Image img;
    public LoginPanel(Image img) {
        super();
        this.img = img;
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, this);
    }
}