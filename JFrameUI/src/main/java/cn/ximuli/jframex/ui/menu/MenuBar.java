package cn.ximuli.jframex.ui.menu;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
@Getter
public class MenuBar extends JMenuBar {
    ResourceLoaderManager resources;
    List<JMenu> menuList;


    @Autowired
    public MenuBar(List<JMenu> menuList, ResourceLoaderManager resources) {
        super();
        this.menuList = menuList;
        this.resources = resources;
        initialize();
    }

    private void initialize() {
        this.setSize(new Dimension(600, 24));
        for (JMenu jMenu : menuList) {
            JMenuMeta jMenuMeta = AnnotationUtils.findAnnotation(jMenu.getClass(), JMenuMeta.class);
            if (StringUtil.isNoneBlank(jMenuMeta.icon())) {
                jMenu.setIcon(resources.getIcon(jMenuMeta.icon()));
            }
            String jMenuName = I18nHelper.getMessage(jMenuMeta.value());
            jMenu.setText(jMenuName);
            jMenu.setMnemonic(jMenuMeta.shortKey());
            add(jMenu);
        }
    }
}
