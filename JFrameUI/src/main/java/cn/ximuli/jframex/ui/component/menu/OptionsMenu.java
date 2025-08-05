package cn.ximuli.jframex.ui.component.menu;

import cn.ximuli.jframex.ui.component.Hintable;
import cn.ximuli.jframex.ui.internalJFrame.CommonInternalJFrame;
import cn.ximuli.jframex.ui.manager.HintManager;
import cn.ximuli.jframex.ui.event.WindowsEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.UISession;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;

import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.event.KeyEvent;

@Slf4j
@Meta(value = "app.menu.options", shortKey = KeyEvent.VK_F, order = 5, id = "app.menu.options")
public class OptionsMenu extends JMenu {
    private final ResourceLoaderManager resources;
    private final JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
    private final JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
    private final JCheckBoxMenuItem unifiedTitleBarMenuItem;
    private final JCheckBoxMenuItem showTitleBarIconMenuItem;
    private final JCheckBoxMenuItem underlineMenuSelectionMenuItem;
    private final JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
    private final JCheckBoxMenuItem animatedLafChangeMenuItem;
    private final JMenuItem showUIDefaultsInspectorMenuItem;
    private final JMenuItem showHintsMenuItem;


    public OptionsMenu(ResourceLoaderManager resources) {
        this.resources = resources;
        windowDecorationsCheckBoxMenuItem = createWindowDecorations();
        menuBarEmbeddedCheckBoxMenuItem = createMenuBarEmbedded();
        unifiedTitleBarMenuItem = createUnifiedTitleBar();
        showTitleBarIconMenuItem = createShowTitleBarIcon();
        underlineMenuSelectionMenuItem = createUnderlineMenuSelection();
        alwaysShowMnemonicsMenuItem = createAlwaysShowMnemonics();
        animatedLafChangeMenuItem = createAnimatedLafChange();
        showUIDefaultsInspectorMenuItem = createShowUIDefaultsInspectorMenuItem();
        showHintsMenuItem = createShowHints();


        if (supportsFlatLafWindowDecorations()) {
            windowDecorationsCheckBoxMenuItem.setSelected(SystemInfo.isLinux
                    ? JFrame.isDefaultLookAndFeelDecorated()
                    : FlatLaf.isUseNativeWindowDecorations());
            menuBarEmbeddedCheckBoxMenuItem.setSelected(UIManager.getBoolean("TitlePane.menuBarEmbedded"));
            unifiedTitleBarMenuItem.setSelected(UIManager.getBoolean("TitlePane.unifiedBackground"));
            showTitleBarIconMenuItem.setSelected(UIManager.getBoolean("TitlePane.showIcon"));
        } else {
            unsupported(windowDecorationsCheckBoxMenuItem);
            unsupported(menuBarEmbeddedCheckBoxMenuItem);
            unsupported(unifiedTitleBarMenuItem);
            unsupported(showTitleBarIconMenuItem);
        }

        if (SystemInfo.isMacOS) {
            unsupported(underlineMenuSelectionMenuItem);
        }

        if ("false".equals(System.getProperty("flatlaf.animatedLafChange"))) {
            animatedLafChangeMenuItem.setSelected(false);
        }

        add(windowDecorationsCheckBoxMenuItem);
        add(menuBarEmbeddedCheckBoxMenuItem);
        add(unifiedTitleBarMenuItem);
        add(showTitleBarIconMenuItem);
        add(underlineMenuSelectionMenuItem);
        add(alwaysShowMnemonicsMenuItem);
        add(animatedLafChangeMenuItem);
        add(showUIDefaultsInspectorMenuItem);
        add(showHintsMenuItem);
    }


    public JCheckBoxMenuItem createWindowDecorations() {
        JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem = new JCheckBoxMenuItem();
        //---- windowDecorationsCheckBoxMenuItem ----
        windowDecorationsCheckBoxMenuItem.setText("Window decorations");
        windowDecorationsCheckBoxMenuItem.addActionListener(e -> windowDecorationsChanged());
        return windowDecorationsCheckBoxMenuItem;
    }

    public JCheckBoxMenuItem createMenuBarEmbedded() {
        JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem = new JCheckBoxMenuItem();
        menuBarEmbeddedCheckBoxMenuItem.setText("Embedded menu bar");
        menuBarEmbeddedCheckBoxMenuItem.addActionListener(e -> menuBarEmbeddedChanged());
        return menuBarEmbeddedCheckBoxMenuItem;
    }

    private JCheckBoxMenuItem createUnifiedTitleBar() {
        JCheckBoxMenuItem unifiedTitleBarMenuItem = new JCheckBoxMenuItem();
        unifiedTitleBarMenuItem.setText("Unified window title bar");
        unifiedTitleBarMenuItem.addActionListener(e -> unifiedTitleBar());
        return unifiedTitleBarMenuItem;
    }

    private JCheckBoxMenuItem createShowTitleBarIcon() {
        JCheckBoxMenuItem showTitleBarIconMenuItem = new JCheckBoxMenuItem();
        showTitleBarIconMenuItem.setText("Show window title bar icon");
        showTitleBarIconMenuItem.addActionListener(e -> showTitleBarIcon());
        return showTitleBarIconMenuItem;
    }

    private  JCheckBoxMenuItem createUnderlineMenuSelection() {
        JCheckBoxMenuItem underlineMenuSelectionMenuItem = new JCheckBoxMenuItem();
        underlineMenuSelectionMenuItem.setText("Use underline menu selection");
        underlineMenuSelectionMenuItem.addActionListener(e -> underlineMenuSelection());
        return underlineMenuSelectionMenuItem;
    }

    private JCheckBoxMenuItem createAlwaysShowMnemonics() {
        JCheckBoxMenuItem alwaysShowMnemonicsMenuItem = new JCheckBoxMenuItem();
        alwaysShowMnemonicsMenuItem.setText("Always show mnemonics");
        alwaysShowMnemonicsMenuItem.addActionListener(e -> alwaysShowMnemonics());
        return alwaysShowMnemonicsMenuItem;
    }


    private JCheckBoxMenuItem createAnimatedLafChange() {
        JCheckBoxMenuItem animatedLafChangeMenuItem = new JCheckBoxMenuItem();
        animatedLafChangeMenuItem.setText("Animated LAF change");
        animatedLafChangeMenuItem.addActionListener(e -> animatedLafChangeChanged());
        return animatedLafChangeMenuItem;
    }
        //---- animatedLafChangeMenuItem ----

    private JMenuItem createShowHints() {
        JMenuItem showHintsMenuItem = new JMenuItem();
        showHintsMenuItem.setText("Show hints");
        showHintsMenuItem.addActionListener(e -> showHintsChanged());
        return showHintsMenuItem;

    }

    private JMenuItem createShowUIDefaultsInspectorMenuItem() {
        JMenuItem showUIDefaultsInspectorMenuItem = new JMenuItem();
        showUIDefaultsInspectorMenuItem.setText("Show UI Defaults Inspector");
        showUIDefaultsInspectorMenuItem.addActionListener(e -> showUIDefaultsInspector());
        return showUIDefaultsInspectorMenuItem;
    }

    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void windowDecorationsChanged() {
        boolean windowDecorations = windowDecorationsCheckBoxMenuItem.isSelected();
        FrameManager.publishEvent(WindowsEvent.createEvent(WindowsEvent.WINDOW_DECORATIONS_CHANGED, windowDecorations));
        this.menuBarEmbeddedCheckBoxMenuItem.setEnabled(windowDecorations);
        this.unifiedTitleBarMenuItem.setEnabled(windowDecorations);
        this.showTitleBarIconMenuItem.setEnabled(windowDecorations);
    }

    private boolean supportsFlatLafWindowDecorations() {
        return FlatLaf.supportsNativeWindowDecorations() || SystemInfo.isLinux;
    }

    private void unsupported(JCheckBoxMenuItem menuItem) {
        menuItem.setEnabled(false);
        menuItem.setSelected(false);
        menuItem.setToolTipText("Not supported on this platform.");
    }

    private void menuBarEmbeddedChanged() {
        UIManager.put("TitlePane.menuBarEmbedded", menuBarEmbeddedCheckBoxMenuItem.isSelected());
        FlatLaf.revalidateAndRepaintAllFramesAndDialogs();
    }

    private void unifiedTitleBar() {
        UIManager.put("TitlePane.unifiedBackground", unifiedTitleBarMenuItem.isSelected());
        FlatLaf.repaintAllFramesAndDialogs();
    }

    private void showTitleBarIcon() {
        boolean showIcon = showTitleBarIconMenuItem.isSelected();
        // for main frame (because already created)
        getRootPane().putClientProperty(FlatClientProperties.TITLE_BAR_SHOW_ICON, showIcon);
        // for other not yet created frames/dialogs
        UIManager.put("TitlePane.showIcon", showIcon);
    }

    private void underlineMenuSelection() {
        UIManager.put("MenuItem.selectionType", underlineMenuSelectionMenuItem.isSelected() ? "underline" : null);
    }

    private void alwaysShowMnemonics() {
        UIManager.put("Component.hideMnemonics", !alwaysShowMnemonicsMenuItem.isSelected());
        repaint();
    }

    private void animatedLafChangeChanged() {
        System.setProperty("flatlaf.animatedLafChange", String.valueOf(animatedLafChangeMenuItem.isSelected()));
    }

    private void showHintsChanged() {
        HintManager.hideAllHints();
        showHints();
    }

    private void showHints() {
        UISession currentUISession = FrameManager.getCurrentUISession();
        if (currentUISession != null) {
            JInternalFrame currentFrame = currentUISession.getStatePanel().getCurrentFrame();
            if (currentFrame == null) {
                log.debug("show main hint");
                currentUISession.getMainFrame().showHint(true);
                return;
            }
            log.debug("show main hint: currentFrame:{}", currentFrame);
            if (currentFrame instanceof Hintable commonInternalJFrame) {
                commonInternalJFrame.showHint(true);
            }
        }
    }

}
