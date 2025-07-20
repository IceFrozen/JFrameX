package cn.ximuli.jframex.ui.panels;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Slf4j
public class ComponentsShowSettingPanel extends JTabbedPane {

    BasicComponentsPanel basicComponentsPanel;
    ContainerComponentsPanel containerComponentsPanel;
    DataComponentsPanel dataComponentsPanel;
    TabsComponentsPanel tabsComponentsPanel;
    OptionPanePanel optionPanePanel;
    ExtrasPanel extrasPanel;

    ResourceLoaderManager resources;

    public ComponentsShowSettingPanel(ResourceLoaderManager resources, BasicComponentsPanel basicComponentsPanel, ContainerComponentsPanel containerComponentsPanel, DataComponentsPanel dataComponentsPanel, TabsComponentsPanel tabsComponentsPanel, OptionPanePanel optionPanePanel, ExtrasPanel extrasPanel) {
        super();
        this.basicComponentsPanel = basicComponentsPanel;
        this.containerComponentsPanel = containerComponentsPanel;
        this.dataComponentsPanel = dataComponentsPanel;
        this.optionPanePanel = optionPanePanel;
        this.extrasPanel = extrasPanel;
        this.resources = resources;
        this.tabsComponentsPanel = tabsComponentsPanel;
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        addTab("Basic Components", basicComponentsPanel);
        addTab("Container Components", containerComponentsPanel);
        addTab("Data Components", dataComponentsPanel);
        addTab("Tabs", tabsComponentsPanel);
        addTab("Option Pane", optionPanePanel);
        addTab("Extras", extrasPanel);

    }
}
