package cn.ximuli.jframex.ui.manager;

import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.component.menu.MenuBar;
import cn.ximuli.jframex.ui.component.menu.ToolBar;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import cn.ximuli.jframex.ui.component.panels.StatePanel;
import cn.ximuli.jframex.ui.internalJFrame.CommonInternalJFrame;
import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.util.SystemInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

@Getter
@Slf4j
public class UISession {
    public static final String KEY_SYSTEM_SCALE_FACTOR = "systemScaleFactor";
    private LoggedInUser currentUser;
    private List<CommonInternalJFrame> internalJFrames;
    private MainFrame mainFrame;
    private ResourceLoaderManager resources;
    private DesktopPanel desktopPanel;
    private StatePanel statePanel;
    private MenuBar menuBar;
    private ToolBar toolBar;

    @Getter
    public List<Runnable> afterUIReady = new ArrayList<>();

    private UISession() {
    }

    public UISession(LoggedInUser user, ResourceLoaderManager resources) {
        this.currentUser = user;
        this.resources = resources;
    }


    public void prepareUI() throws ClassNotFoundException {
        desktopPanel = new DesktopPanel(this.resources);
        statePanel = new StatePanel(currentUser);
        internalJFrames = initInternalJFrames();
        menuBar = initMenuBar();
        toolBar = initToolBar();
        mainFrame = new MainFrame(resources, desktopPanel, toolBar, statePanel, menuBar);
        registerSystemScaleFactors(mainFrame);
        this.afterUIReady.forEach(Runnable::run);
    }


    public List<CommonInternalJFrame> initInternalJFrames() throws ClassNotFoundException {
        Set<Class<? extends CommonInternalJFrame>> classes = SpringUtils.scanClasses(Application.APP_INTERNAL_FRAME_PACKAGE, CommonInternalJFrame.class);
        return UICreator.createCommonInternalJFrame(desktopPanel, classes.toArray(new Class[0]));

    }

    private MenuBar initMenuBar() throws ClassNotFoundException {
        List<JMenu> jMenuList = UICreator.createJMenuList();
        return new MenuBar(jMenuList, resources);
    }

    private ToolBar initToolBar() {
        return new ToolBar(menuBar, resources);
    }

    public void destory() {
        this.afterUIReady.clear();
        cleanupComponent(this.mainFrame);
        cleanupComponent(this.desktopPanel);
        cleanupComponent(this.statePanel);
        cleanupComponent(this.menuBar);
        cleanupComponent(this.toolBar);
        for (CommonInternalJFrame internalJFrame : internalJFrames) {
            cleanupComponent(internalJFrame);
        }
    }

    public void registerSystemScaleFactors(JFrame frame) {
        registerSystemScaleFactor(frame, "alt shift F1", null);
        registerSystemScaleFactor(frame, "alt shift F2", "1");

        if (SystemInfo.isWindows) {
            registerSystemScaleFactor(frame, "alt shift F3", "1.25");
            registerSystemScaleFactor(frame, "alt shift F4", "1.5");
            registerSystemScaleFactor(frame, "alt shift F5", "1.75");
            registerSystemScaleFactor(frame, "alt shift F6", "2");
            registerSystemScaleFactor(frame, "alt shift F7", "2.25");
            registerSystemScaleFactor(frame, "alt shift F8", "2.5");
            registerSystemScaleFactor(frame, "alt shift F9", "2.75");
            registerSystemScaleFactor(frame, "alt shift F10", "3");
            registerSystemScaleFactor(frame, "alt shift F11", "3.5");
            registerSystemScaleFactor(frame, "alt shift F12", "4");
        } else {
            // Java on macOS and Linux supports only integer scale factors
            registerSystemScaleFactor(frame, "alt shift F3", "2");
            registerSystemScaleFactor(frame, "alt shift F4", "3");
            registerSystemScaleFactor(frame, "alt shift F5", "4");
        }
    }


    private static void registerSystemScaleFactor(JFrame frame, String keyStrokeStr, String scaleFactor) {
        log.info("register:frame: {}, keyStrokeStr:{}, scaleFactor: {}", frame, keyStrokeStr, scaleFactor);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeStr);
        if (keyStroke == null)
            throw new IllegalArgumentException("Invalid key stroke '" + keyStrokeStr + "'");

        ((JComponent) frame.getContentPane()).registerKeyboardAction(
                e -> applySystemScaleFactor(frame, scaleFactor),
                keyStroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private static void applySystemScaleFactor(JFrame frame, String scaleFactor) {
        //TODO i8
        if (JOptionPane.showConfirmDialog(frame,
                "Change system scale factor to "
                        + (scaleFactor != null ? scaleFactor : "default")
                        + " and exit?",
                frame.getTitle(), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        if (scaleFactor != null)
            JFramePref.state.put(KEY_SYSTEM_SCALE_FACTOR, scaleFactor);
        else {
            JFramePref.state.remove(KEY_SYSTEM_SCALE_FACTOR);
        }

        System.exit(0);
    }

    public static void initSystemScale() {
        if (System.getProperty("sun.java2d.uiScale") == null) {
            String scaleFactor = JFramePref.state.get(KEY_SYSTEM_SCALE_FACTOR, null);
            if (scaleFactor != null) {
                System.setProperty("sun.java2d.uiScale", scaleFactor);
                log.info("JFrameX: setting 'sun.java2d.uiScale' to {}", scaleFactor);
                log.info("use 'Alt+Shift+F1...12' to change it to 1x...4x");
            }
        }
    }

    public <T> T getInternalJFrame(Class<T> classType) {
        for (CommonInternalJFrame internalJFrame : internalJFrames) {
            if (classType.isInstance(internalJFrame)) {
                return classType.cast(internalJFrame);
            }
        }
        return null;
    }

    private static void cleanupComponent(java.awt.Component component) {
        if (component == null) {
            return;
        }
        log.debug("Start to clean components: {}: {}", component.getName(), component);
        try {
            // 1. Remove from parent container
            Container parent = component.getParent();
            if (parent != null) {
                log.debug("Start to clean components parent: {}: {}", parent.getName(), parent);
                parent.remove(component);
                parent.revalidate();
                parent.repaint();
            }

            // 2. Remove event listeners
            // Action listeners for buttons
            if (component instanceof AbstractButton button) {
                log.debug("Start to clean button : {}: {}", button.getName(), button);
                Arrays.stream(button.getActionListeners()).forEach(button::removeActionListener);
            }

            // Mouse-related listeners
            Arrays.stream(component.getMouseListeners()).forEach(component::removeMouseListener);
            Arrays.stream(component.getMouseMotionListeners()).forEach(component::removeMouseMotionListener);
            Arrays.stream(component.getMouseWheelListeners()).forEach(component::removeMouseWheelListener);

            // Keyboard and focus listeners
            Arrays.stream(component.getKeyListeners()).forEach(component::removeKeyListener);
            Arrays.stream(component.getFocusListeners()).forEach(component::removeFocusListener);

            // Component and property change listeners
            Arrays.stream(component.getComponentListeners()).forEach(component::removeComponentListener);
            Arrays.stream(component.getPropertyChangeListeners()).forEach(component::removePropertyChangeListener);

            // 3. Handle containers
            if (component instanceof Container container) {
                container.removeAll(); // Remove all child components
                // Recursively clean up child components
                Arrays.stream(container.getComponents()).forEach(UISession::cleanupComponent);
            }

            // 4. Handle top-level windows
            if (component instanceof Window window) {
                Arrays.stream(window.getWindowListeners()).forEach(window::removeWindowListener);
                Arrays.stream(window.getWindowStateListeners()).forEach(window::removeWindowStateListener);
                window.dispose(); // Release native resources
            }

            // 5. Clear client properties for JComponents
            if (component instanceof JComponent jComponent) {
                jComponent.removeAll();
            }

        } catch (Exception e) {
            log.error("Error during component cleanup: " + e.getMessage(), e);
        }
    }

    public void registerKeyAction(int key, BiConsumer<MainFrame, ActionEvent> keyAction) {
        Runnable runnable = () -> {
            ((JComponent) mainFrame.getContentPane()).registerKeyboardAction(
                    e -> keyAction.accept(mainFrame, e),
                    KeyStroke.getKeyStroke(key, 0, false),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        };

        if (this.mainFrame == null) {
            this.afterUIReady.add(runnable);
        } else {
            runnable.run();
        }
    }
}
