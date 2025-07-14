package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.intellijthemes.IJThemesPanel;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.menu.MenuBar;
import cn.ximuli.jframex.ui.menu.ToolBar2;
import cn.ximuli.jframex.ui.panels.*;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


@org.springframework.stereotype.Component
@Slf4j
public class SettingInternalJFrame extends CommonInternalJFrame {
    private JTabbedPane tabbedPane;
    private ControlBar controlBar;
    @Getter
    IJThemesPanel themesPanel;
    private final String[] availableFontFamilyNames;

    public SettingInternalJFrame(ResourceLoaderManager resources, JDesktopPane desktopPane) {
        super(resources, desktopPane);
        setFrameIcon(resources.getIcon("settings"));
        setTitle(I18nHelper.getMessage("app.setting.title"));
        availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
        Arrays.sort(availableFontFamilyNames);
        initComponents();
        initFullWindowContent();

    }

    private void initFullWindowContent() {
        if (!supportsFlatLafWindowDecorations())
            return;

        // create fullWindowContent mode toggle button
        MenuBar menuBar = SpringUtils.getBean(MenuBar.class);
        ToolBar2 toolBar = SpringUtils.getBean(ToolBar2.class);
        Icon expandIcon = new FlatSVGIcon("com/formdev/flatlaf/demo/icons/expand.svg");
        Icon collapseIcon = new FlatSVGIcon("com/formdev/flatlaf/demo/icons/collapse.svg");
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
        JPanel contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        BasicComponentsPanel basicComponentsPanel = new BasicComponentsPanel(resources);
        ContainerComponentsPanel containerComponentsPanel = new ContainerComponentsPanel(resources);
        DataComponentsPanel dataComponentsPanel = new DataComponentsPanel(resources);
        TabInternalJFrame.TabsPanel tabsPanel = new TabInternalJFrame.TabsPanel(resources);
        OptionPanePanel optionPanePanel = new OptionPanePanel(resources);
        ExtrasPanel extrasPanel = new ExtrasPanel(resources);
        controlBar = new ControlBar(this,tabbedPane);
        JPanel themesPanelPanel = new JPanel();
        JPanel winFullWindowContentButtonsPlaceholder = new JPanel();
        themesPanel = new IJThemesPanel(resources);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPanel.setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[grow,fill]"));


        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("Basic Components", basicComponentsPanel);
        tabbedPane.addTab("Container Components", containerComponentsPanel);
        tabbedPane.addTab("Data Components", dataComponentsPanel);
        tabbedPane.addTab("Tabs", tabsPanel);
        tabbedPane.addTab("Option Pane", optionPanePanel);
        tabbedPane.addTab("Extras", extrasPanel);

        contentPanel.add(tabbedPane, "cell 0 0");

        contentPane.add(contentPanel, BorderLayout.CENTER);
        contentPane.add(controlBar, BorderLayout.PAGE_END);


        themesPanelPanel.setLayout(new BorderLayout());
        winFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
        themesPanelPanel.add(winFullWindowContentButtonsPlaceholder, BorderLayout.NORTH);
        themesPanelPanel.add(themesPanel, BorderLayout.CENTER);
        contentPane.add(themesPanelPanel, BorderLayout.LINE_END);

        // remove contentPanel bottom insets
        MigLayout layout = (MigLayout) contentPanel.getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{
                insets[0],
                insets[1],
                new UnitValue(0, UnitValue.MAX_SIZE, null),
                insets[3]
        });
        layout.setLayoutConstraints(lc);
    }

    @Override
    protected void initUI() {

    }
}
