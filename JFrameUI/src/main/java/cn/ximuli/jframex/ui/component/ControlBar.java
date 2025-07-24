package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.*;

@Slf4j
public class ControlBar extends JPanel {
    private JInternalFrame parentFrame;
    private JTabbedPane tabbedPane;
    private JSeparator separator1;
    private LookAndFeelsComboBox lookAndFeelComboBox;
    private JCheckBox rightToLeftCheckBox;
    private JCheckBox enabledCheckBox;
    private JLabel infoLabel;

    public ControlBar(JInternalFrame parentFrame, JTabbedPane tabbedPane) {
        this.parentFrame = parentFrame;
        this.tabbedPane = tabbedPane;
        initComponents();
        // remove top insets
        MigLayout layout = (MigLayout) getLayout();
        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
        UnitValue[] insets = lc.getInsets();
        lc.setInsets(new UnitValue[]{
                new UnitValue(0, UnitValue.PIXEL, null),
                insets[1],
                insets[2],
                insets[3]
        });
        layout.setLayoutConstraints(lc);

        // initialize look and feels combo box
        DefaultComboBoxModel<LookAndFeelInfo> lafModel = new DefaultComboBoxModel<>();
        lafModel.addElement(new LookAndFeelInfo("FlatLaf Light (F1)", FlatLightLaf.class.getName()));
        lafModel.addElement(new LookAndFeelInfo("FlatLaf Dark (F2)", FlatDarkLaf.class.getName()));
        lafModel.addElement(new LookAndFeelInfo("FlatLaf IntelliJ (F3)", FlatIntelliJLaf.class.getName()));
        lafModel.addElement(new LookAndFeelInfo("FlatLaf Darcula (F4)", FlatDarculaLaf.class.getName()));
        lafModel.addElement(new LookAndFeelInfo("FlatLaf macOS Light (F5)", FlatMacLightLaf.class.getName()));
        lafModel.addElement(new LookAndFeelInfo("FlatLaf macOS Dark (F6)", FlatMacDarkLaf.class.getName()));

        LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo lookAndFeel : lookAndFeels) {
            String name = lookAndFeel.getName();
            String className = lookAndFeel.getClassName();
            if (className.equals("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel") ||
                    className.equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel"))
                continue;

            if ((SystemInfo.isWindows && className.equals("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")) ||
                    (SystemInfo.isMacOS && className.equals("com.apple.laf.AquaLookAndFeel")) ||
                    (SystemInfo.isLinux && className.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")))
                name += " (F9)";
            else if (className.equals(MetalLookAndFeel.class.getName()))
                name += " (F12)";
            else if (className.equals(NimbusLookAndFeel.class.getName()))
                name += " (F11)";

            lafModel.addElement(new LookAndFeelInfo(name, className));
        }

        lookAndFeelComboBox.setModel(lafModel);
        ThemeUIManager.themeChangeListener(this::updateInfoLabel);
        ThemeUIManager.UIScaleChangeListener(this::updateInfoLabel);
        initialize();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (infoLabel != null)
            updateInfoLabel();
    }

    void initialize() {
        // register F1, F2, ... keys to switch to Light, Dark or other LaFs
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F1, FlatLightLaf.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F2, FlatDarkLaf.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F3, FlatIntelliJLaf.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F4, FlatDarculaLaf.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F5, FlatMacLightLaf.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F6, FlatMacDarkLaf.class.getName());

        if (SystemInfo.isWindows)
            registerSwitchToLookAndFeelKey(KeyEvent.VK_F9, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        else if (SystemInfo.isMacOS)
            registerSwitchToLookAndFeelKey(KeyEvent.VK_F9, "com.apple.laf.AquaLookAndFeel");
        else if (SystemInfo.isLinux)
            registerSwitchToLookAndFeelKey(KeyEvent.VK_F9, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F11, NimbusLookAndFeel.class.getName());
        registerSwitchToLookAndFeelKey(KeyEvent.VK_F12, MetalLookAndFeel.class.getName());
        updateUI();
    }

    private void updateInfoLabel() {
        String javaVendor = System.getProperty("java.vendor");
        if ("Oracle Corporation".equals(javaVendor))
            javaVendor = null;
        double systemScaleFactor = UIScale.getSystemScaleFactor(getGraphicsConfiguration());
        float userScaleFactor = UIScale.getUserScaleFactor();
        Font font = UIManager.getFont("Label.font");
        String newInfo = "(Java " + System.getProperty("java.version")
                + (javaVendor != null ? ("; " + javaVendor) : "")
                + (systemScaleFactor != 1 ? (";  system scale factor " + systemScaleFactor) : "")
                + (userScaleFactor != 1 ? (";  user scale factor " + userScaleFactor) : "")
                + (systemScaleFactor == 1 && userScaleFactor == 1 ? "; no scaling" : "")
                + "; " + font.getFamily() + " " + font.getSize()
                + (font.isBold() ? " BOLD" : "")
                + (font.isItalic() ? " ITALIC" : "")
                + ")";

        if (!newInfo.equals(infoLabel.getText()))
            infoLabel.setText(newInfo);
    }

    private void registerSwitchToLookAndFeelKey(int keyCode, String lafClassName) {
        FrameManager.registerKeyAction(
                keyCode,
                (mainFrame, event) -> selectLookAndFeel(lafClassName));

    }

    private void selectLookAndFeel(String lafClassName) {
        lookAndFeelComboBox.setSelectedLookAndFeel(lafClassName);
    }

    private void lookAndFeelChanged() {
        String lafClassName = lookAndFeelComboBox.getSelectedLookAndFeel();
        if (lafClassName == null || lafClassName.equals(UIManager.getLookAndFeel().getClass().getName())) {
            return;
        }
        ThemeUIManager.lookAndFeelChanged(lafClassName);
    }

    private void rightToLeftChanged() {
        boolean rightToLeft = rightToLeftCheckBox.isSelected();
        rightToLeftChanged(parentFrame, rightToLeft);
    }

    private void rightToLeftChanged(Container c, boolean rightToLeft) {
        c.applyComponentOrientation(rightToLeft
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT);
        c.revalidate();
        c.repaint();
    }

    private void enabledChanged() {
        enabledDisable(tabbedPane, enabledCheckBox.isSelected());
        tabbedPane.repaint();
    }

    private void enabledDisable(Container container, boolean enabled) {
        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                enabledDisable((JPanel) c, enabled);
                continue;
            }

            c.setEnabled(enabled);

            if (c instanceof JScrollPane) {
                Component view = ((JScrollPane) c).getViewport().getView();
                if (view != null)
                    view.setEnabled(enabled);
            } else if (c instanceof JTabbedPane) {
                JTabbedPane tabPane = (JTabbedPane) c;
                int tabCount = tabPane.getTabCount();
                for (int i = 0; i < tabCount; i++) {
                    Component tab = tabPane.getComponentAt(i);
                    if (tab != null)
                        tab.setEnabled(enabled);
                }
            }

            if (c instanceof JToolBar) {
                enabledDisable((JToolBar) c, enabled);
            }
        }
    }

    private void initComponents() {
        separator1 = new JSeparator();
        lookAndFeelComboBox = new LookAndFeelsComboBox();
        rightToLeftCheckBox = new JCheckBox();
        enabledCheckBox = new JCheckBox();
        infoLabel = new JLabel();

        setLayout(new MigLayout(
                "insets dialog",
                // columns
                "[fill]" +
                        "[fill]" +
                        "[fill]" +
                        "[grow,fill]" +
                        "[button,fill]",
                // rows
                "[bottom]" +
                        "[]"));
        add(separator1, "cell 0 0 5 1");

        //---- lookAndFeelComboBox ----
        lookAndFeelComboBox.addActionListener(e -> lookAndFeelChanged());
        add(lookAndFeelComboBox, "cell 0 1");

        //---- rightToLeftCheckBox ----
        rightToLeftCheckBox.setText("right-to-left");
        rightToLeftCheckBox.setMnemonic('R');
        rightToLeftCheckBox.addActionListener(e -> rightToLeftChanged());
        add(rightToLeftCheckBox, "cell 1 1");

        //---- enabledCheckBox ----
        enabledCheckBox.setText("enabled");
        enabledCheckBox.setMnemonic('E');
        enabledCheckBox.setSelected(true);
        enabledCheckBox.addActionListener(e -> enabledChanged());
        add(enabledCheckBox, "cell 2 1");

        infoLabel.setText("text");
        add(infoLabel, "cell 3 1,alignx center,growx 0");

    }

}