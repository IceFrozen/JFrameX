package cn.ximuli.jframex.ui.component.menu;


import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.manager.UICreator;
import cn.ximuli.jframex.ui.test.Jinhuo_Tuihuo_IFram;
import cn.ximuli.jframex.ui.internalJFrame.UserInternalJFrame;
import cn.ximuli.jframex.ui.event.MenuButtonClickEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.test.KeHuGuanLi;
import cn.ximuli.jframex.ui.test.KuCunPanDian;
import cn.ximuli.jframex.ui.test.XiaoShouDan;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Mate(value = "app.menu.user.title", order = 6)
public class UserMenu extends JMenu {
    ResourceLoaderManager resources;

    public UserMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        UICreator.createJMenuItemForInternalJFrame(
                UserInternalJFrame.class,
                Jinhuo_Tuihuo_IFram.class,
                KuCunPanDian.class,
                KeHuGuanLi.class,
                XiaoShouDan.class
        ).forEach(this::add);
    }

    public void createJMenuItem() {

//        List<JMenuItem> result = new ArrayList<>();
//        JMenuItem item = new JMenuItem();
//
//        UserInternalJFrame jInternalFrame = SpringUtils.getBean(UserInternalJFrame.class);
//        setTitle(I18nHelper.getMessage(UserInternalJFrame.class.getAnnotation(Mate.class).value()));
//        setFrameIcon(resources.getIcon(UserInternalJFrame.class.getAnnotation(Mate.class).icon()));
//        item.setText(jInternalFrame.getTitle());
//        item.setIcon(jInternalFrame.getFrameIcon());
//        item.putClientProperty("class", jInternalFrame.getClass());
//        item.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item)));
//        result.add(item);
//
//
//        JMenuItem item2 = new JMenuItem();
//        item2.setText("Jinhuo_Tuihuo_IFram");
//        item2.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item2.putClientProperty("class", Jinhuo_Tuihuo_IFram.class);
//        item2.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item2)));
//        result.add(item2);
//
//        JMenuItem item3 = new JMenuItem();
//        item3.setText("KuCunPanDian");
//        item3.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item3.putClientProperty("class", KuCunPanDian.class);
//        item3.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item3)));
//        result.add(item3);
//
//
//        JMenuItem item4 = new JMenuItem();
//        item4.setText("KeHuGuanLi");
//        item4.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item4.putClientProperty("class", KeHuGuanLi.class);
//        item4.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item4)));
//        result.add(item4);
//
//
//        JMenuItem item5 = new JMenuItem();
//        item5.setText("KeHuGuanLi");
//        item5.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item5.putClientProperty("class", XiaoShouDan.class);
//        item5.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item5)));
//        result.add(item5);



//
//        JMenuItem item3 = new JMenuItem();
//        item3.setText("list2");
//        item3.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item3.putClientProperty("class", JinHuoTuiHuo.class);
//        item3.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item3)));
//
//        JMenuItem item4 = new JMenuItem();
//        item4.setText("list2");
//        item4.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item4.putClientProperty("class", Sample1InternalFrame.class);
//        item4.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item4)));
//
//        JMenuItem item5 = new JMenuItem();
//        item5.setText("list2");
//        item5.setIcon(resources.getIcon("icons/jinhuo_tuihuo"));
//        item5.putClientProperty("class", Jinhuo_Tuihuo_IFram.class);
//        item5.addActionListener(e -> FrameManager.publishEvent(new MenuButtonClickEvent(item5)));
    }

}
