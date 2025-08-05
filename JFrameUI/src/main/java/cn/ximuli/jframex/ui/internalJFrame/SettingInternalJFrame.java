package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.ControlBar;
import cn.ximuli.jframex.ui.component.Hintable;
import cn.ximuli.jframex.ui.component.menu.Meta;
import cn.ximuli.jframex.ui.component.panels.*;
import cn.ximuli.jframex.ui.manager.HintManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.menu.ToolBar;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.ReflectionUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

@Meta(value = "app.setting", icon = "icons/settings", order = 6, id = "app.setting")
@Slf4j
public class SettingInternalJFrame extends CommonInternalJFrame implements Hintable {
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
        setTitle(I18nHelper.getMessage(getClass().getAnnotation(Meta.class).value()));
        setFrameIcon(resources.getIcon(getClass().getAnnotation(Meta.class).icon()));
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

        settingListPanel.addSelectedAction(this::settingPanelShow);

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


    public void refreshUI() {
        initComponents();
        initFullWindowContent();
    }

    @Override
    public void showHint(boolean reload) {
        if (reload) {
            clearHint();
        }
        showPanelHint(reload);

        HintManager.Hint controlBarthemesHint = new HintManager.Hint(
                "Use 'control bar' list to set themes.",
                controlBar.getLookAndFeelComboBox(), SwingConstants.TOP, "hint.control.themes", null);

        HintManager.Hint themesHint = new HintManager.Hint(
                "Use 'Themes' list to try out various themes.",
                themesPanel.getThemesPanel(), SwingConstants.LEFT, "hint.themes", controlBarthemesHint);
        HintManager.showHint(themesHint);
    }

    @Override
    public void clearHint() {
        JFramePref.state.remove("hint.themes");
        JFramePref.state.remove("hint.control.themes");
    }


    private void settingPanelShow(SettingInfo<JComponent> settingInfo) {
        if (ArrayUtils.contains(controlPanel.getComponents(), settingInfo.getValue())) {
            return;
        }
        JComponent component =  settingInfo.getValue();
        controlPanel.removeAll();
        controlPanel.add(component, BorderLayout.CENTER);
        controlPanel.revalidate();
        controlPanel.updateUI();
        if (component instanceof Hintable hintable) {
            hintable.showHint(false);
        }

    }


    public void showPanelHint(boolean reload) {
        try {
            SettingInfo<JComponent> currenSettings = settingListPanel.getCurrenSettings();
            JComponent currentComponent = currenSettings.getValue();
            log.debug("afterShow... currentComponent:{}", currenSettings.getValue());
            Method showHint = currentComponent.getClass().getMethod("showHint", boolean.class);
            ReflectionUtils.invokeMethod(showHint, currentComponent, reload);
        } catch (NoSuchMethodException Ignore) {
            log.info("Ignore Exception");
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
