package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.ControlBar;
import cn.ximuli.jframex.ui.component.panels.ComponentsShowSettingPanel;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.SettingListPanel;
import cn.ximuli.jframex.ui.component.panels.ThemesPanelPanel;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.menu.ToolBar;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@org.springframework.stereotype.Component
@Slf4j
public class SettingInternalJFrame extends CommonInternalJFrame {
    private ComponentsShowSettingPanel tabbedPane;
    private ControlBar controlBar;
    @Getter
    private ThemesPanelPanel themesPanel;
    @Getter
    private Container settingContentPane;
    private JPanel controlPanel;
    @Getter
    private SettingListPanel settingListPanel;


    public SettingInternalJFrame(ResourceLoaderManager resources, DesktopPanel desktopPane) {
        super(resources, desktopPane);
        setFrameIcon(resources.getIcon("settings"));
        setTitle(I18nHelper.getMessage("app.setting.title"));
    }

    private void initFullWindowContent() {
        if (!supportsFlatLafWindowDecorations())
            return;

        // create fullWindowContent mode toggle button
        MenuBar menuBar = SpringUtils.getBean(MenuBar.class);
        ToolBar toolBar = SpringUtils.getBean(ToolBar.class);
        Icon expandIcon = resources.getIcon("icons/expand");
        Icon collapseIcon = resources.getIcon("icons/collapse");
        JToggleButton fullWindowContentButton = new JToggleButton(expandIcon);
        fullWindowContentButton.setToolTipText("Toggle full window content");
        fullWindowContentButton.addActionListener(e -> {
            boolean fullWindowContent = fullWindowContentButton.isSelected();
            fullWindowContentButton.setIcon(fullWindowContent ? collapseIcon : expandIcon);
            menuBar.setVisible(!fullWindowContent);
            toolBar.setVisible(!fullWindowContent);
            getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, fullWindowContent);
        });

        // add fullWindowContent mode toggle button to tabbed pane
        JToolBar trailingToolBar = new JToolBar();
        trailingToolBar.add(Box.createGlue());
        trailingToolBar.add(fullWindowContentButton);
        tabbedPane.putClientProperty(FlatClientProperties.TABBED_PANE_TRAILING_COMPONENT, trailingToolBar);
    }

    private boolean supportsFlatLafWindowDecorations() {
        return FlatLaf.supportsNativeWindowDecorations() || SystemInfo.isLinux;
    }

    private void initComponents() {
        this.tabbedPane = new ComponentsShowSettingPanel(resources);
        this.themesPanel = new ThemesPanelPanel(resources);
        this.settingListPanel = new SettingListPanel(resources);
        this.settingContentPane = getContentPane();
        this.controlPanel = new JPanel();
        this.controlBar = new ControlBar(this, tabbedPane);
        this.settingContentPane.setLayout(new BorderLayout());
        this.controlPanel.setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[grow,fill]"));

        settingContentPane.add(controlPanel, BorderLayout.CENTER);
        settingContentPane.add(settingListPanel, BorderLayout.LINE_START);
        settingContentPane.add(themesPanel, BorderLayout.LINE_END);
        settingContentPane.add(controlBar, BorderLayout.PAGE_END);

        settingListPanel.addSelectedAction(info -> {
            JComponent bean = SpringUtils.getBean(info.getClz());
            controlPanel.removeAll();
            controlPanel.add(bean, BorderLayout.CENTER);
            refreshUI();
        });

        // remove contentPanel bottom insets
        MigLayout layout = (MigLayout) controlPanel.getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{
                insets[0],
                insets[1],
                new UnitValue(0, UnitValue.MAX_SIZE, null),
                insets[3]
        });
        layout.setLayoutConstraints(lc);
        settingListPanel.select(0);
    }

    @Override
    protected void refleshUI() {
        initComponents();
        initFullWindowContent();
    }

    protected void refreshUI(){
        controlPanel.revalidate();
        controlPanel.updateUI();
    }
}
