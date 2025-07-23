package cn.ximuli.jframex.ui.panels;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
@Slf4j
@SettingMenu(value = "app.setting.item.components.all", category = "app.setting.item.category.components", toolTipText = "app.setting.item.components.all.toolTipText", order = 1)
public class ComponentsShowSettingPanel extends JTabbedPane {

    BasicComponentsPanel basicComponentsPanel;
    ContainerComponentsPanel containerComponentsPanel;
    DataComponentsPanel dataComponentsPanel;
    TabsComponentsPanel tabsComponentsPanel;
    OptionPanePanel optionPanePanel;
    ExtrasPanel extrasPanel;

    ResourceLoaderManager resources;

    public ComponentsShowSettingPanel(ResourceLoaderManager resources) {
        super();
        this.basicComponentsPanel =   new BasicComponentsPanel(resources);
        this.containerComponentsPanel = new ContainerComponentsPanel(resources);
        this.dataComponentsPanel = new DataComponentsPanel(resources);
        this.optionPanePanel = new OptionPanePanel(resources);
        this.extrasPanel = new ExtrasPanel(resources);
        this.resources = resources;
        this.tabsComponentsPanel = new TabsComponentsPanel(resources);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        addTab("Basic Components", basicComponentsPanel);
        addTab("Container Components", containerComponentsPanel);
        addTab("Data Components", dataComponentsPanel);
        addTab("Tabs", tabsComponentsPanel);
        addTab("Option Pane", optionPanePanel);
        addTab("Extras", extrasPanel);
    }

}
