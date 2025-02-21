package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.StatePanel;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;


@Component
@Getter
public class MenuBar extends JMenuBar {
    ResourceLoaderManager resources;
    private JDesktopPane desktopPanel;

    private Map<JMenuItem, JInternalFrame> iFrames = null;
    private int nextFrameX, nextFrameY;
    List<JMenu> menuList;
    @Autowired
    private StatePanel statuePanel;



    @Autowired
    public MenuBar(JDesktopPane desktopPanel, List<JMenu> menuList,  ResourceLoaderManager resources) {
        super();
        this.desktopPanel = desktopPanel;
        this.menuList = menuList;
        this.resources = resources;
        initialize();
    }

    private void initialize() {
        this.setSize(new Dimension(600, 24));
        for (JMenu jMenu : menuList) {
            JMenuMeta jMenuMeta = AnnotationUtils.findAnnotation(jMenu.getClass(), JMenuMeta.class);
            JMenuIcon jMenuIcon = AnnotationUtils.findAnnotation(jMenu.getClass(), JMenuIcon.class);
            if (jMenuIcon != null && StringUtil.isNoneBlank(jMenuIcon.value())) {
                ImageIcon icon = resources.getIcon(jMenuIcon.value());
                jMenu.setIcon(icon);
            }
            String jMenuName = I18nHelper.getMessage(jMenuMeta.value());
            jMenu.setText(jMenuName);
            jMenu.setMnemonic(jMenuMeta.shortKey());
            add(jMenu);
        }
    }

    public JInternalFrame createIFrame(JMenuItem item, Class clazz) {
        Constructor constructor = clazz.getConstructors()[0];
        JInternalFrame iFrame = iFrames.get(item);
        try {
            if (iFrame == null || iFrame.isClosed()) {
                iFrame = (JInternalFrame) constructor
                        .newInstance(new Object[] {});
                iFrames.put(item, iFrame);
                iFrame.setFrameIcon(item.getIcon());
                iFrame.setLocation(nextFrameX, nextFrameY);
                int frameH = iFrame.getPreferredSize().height;
                int panelH = iFrame.getContentPane().getPreferredSize().height;
                int fSpacing = frameH - panelH;
                nextFrameX += fSpacing;
                nextFrameY += fSpacing;
                if (nextFrameX + iFrame.getWidth() > desktopPanel.getWidth())
                    nextFrameX = 0;
                if (nextFrameY + iFrame.getHeight() > desktopPanel.getHeight())
                    nextFrameY = 0;
                desktopPanel.add(iFrame);
                iFrame.setResizable(false);
                iFrame.setMaximizable(false);
                iFrame.setVisible(true);
            }
            iFrame.setSelected(true);
            statuePanel.setStateLabelText(iFrame.getTitle());
            iFrame.addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameActivated(InternalFrameEvent e) {
                    super.internalFrameActivated(e);
                    JInternalFrame frame = e.getInternalFrame();
                    statuePanel.setStateLabelText(frame.getTitle());
                }

                public void internalFrameDeactivated(InternalFrameEvent e) {
                    statuePanel.setStateLabelText(I18nHelper.getMessage("app.mainframe.state.selected"));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iFrame;
    }
}
