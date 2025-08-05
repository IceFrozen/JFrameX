package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.Getter;

import org.springframework.core.annotation.AnnotationUtils;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

@Getter
public class MenuBar extends JMenuBar {
    public static final String MENU_SYNC_TOOL_BAR_KEY = "jframex.menu.sync.tool.bar.key";
    public static final String MENU_SYNC_TOOL_BAR_TYPE_SIMPLE = "simple";       // just show icon in tool bar

    ResourceLoaderManager resources;
    List<JMenu> menuList;

    public MenuBar(List<JMenu> menuList, ResourceLoaderManager resources) {
        super();
        this.menuList = orderMenu(menuList);
        this.resources = resources;
        initialize();
    }

    private void initialize() {
        for (JMenu jMenu : menuList) {
            Meta meta = AnnotationUtils.findAnnotation(jMenu.getClass(), Meta.class);
            if (StringUtil.isNoneBlank(meta.icon())) {
                jMenu.setIcon(resources.getIcon(meta.icon()));
            }
            String jMenuName = I18nHelper.getMessage(meta.value());
            jMenu.setText(jMenuName);
            jMenu.setMnemonic(meta.shortKey());
            add(jMenu);
        }
        boolean fullScreenableBar = ConvertUtil.toBool(System.getProperty(Application.MAC.APPLE_LAF_USE_SCREEN_MENUBAR));
        if (!fullScreenableBar) {
            setBorder(BorderFactory.createEmptyBorder(14, 80, 5, 0));
        }
    }
    private List<JMenu> orderMenu(List<JMenu> menuList) {
        menuList.sort(Comparator.comparingInt(this::getOrder));
        return menuList;
    }

    private int getOrder(JMenu menu) {
        Meta annotation = menu.getClass().getAnnotation(Meta.class);
        return (annotation != null) ? annotation.order() : Integer.MAX_VALUE; // 默认值处理
    }

}
