package cn.ximuli.jframex.ui.test;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
public class InputKeyListener extends KeyAdapter {
    public void keyTyped(KeyEvent e) {
        String key="-0123456789"+(char)8;
        if(key.indexOf(e.getKeyChar())<0){
            e.consume();
        }
    }
}