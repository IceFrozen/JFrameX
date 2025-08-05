package cn.ximuli.jframex.ui.internalJFrame;

import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.menu.Meta;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.component.panels.DesktopPanel;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;

@Meta(value = "app.menu.file.new", icon = "icons/newFolder", order = 1, id = "app.menu.file.new")
@Slf4j
public class CreateNewInternalJFrame extends CommonInternalJFrame {
    private Pair<JLabel, JTextField> nameLine;
    private JPanel dialogPane;
    private JPanel contentPanel;

    private Pair<JLabel, JComboBox<String>> packageLine;
    private Pair<JLabel, JComboBox<String>> typeLine;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JPopupMenu popupMenu;

    public CreateNewInternalJFrame(ResourceLoaderManager resources, DesktopPanel desktopPane) {
        super(resources, desktopPane);
        this.setSize(600, 320);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(I18nHelper.getMessage(getClass().getAnnotation(Meta.class).value()));
        setFrameIcon(resources.getIcon(getClass().getAnnotation(Meta.class).icon()));
    }


    public void refreshUI() {
        initComponents();
        getRootPane().setDefaultButton(okButton);
        // register ESC key to close frame
        ((JComponent) getContentPane()).registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void okActionPerformed() {
        dispose();
    }

    private void cancelActionPerformed() {
        dispose();
    }

    @Override
    public void componentShown(ComponentEvent e) {
        Dimension parentSize = desktopPane.getSize();
        int width = parentSize.width / 6;
        int height = parentSize.height / 3;
        setSize(width, height);
        int x = (parentSize.width - width) / 2;
        int y = (parentSize.height - height) / 2;
        setLocation(x, y);
    }

    @Override
    public void showHint(boolean b) {

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        popupMenu = createJPopupMenu();
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        dialogPane.setLayout(new BorderLayout());
        //======== contentPanel ========
        contentPanel.setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[fill]" +
                        "[grow,fill]",
                // rows
                "[]" +
                        "[]" +
                        "[]"));
        nameLine = createLabelComponent("app.menu.file.new.name.label", () -> {
            JTextField jTextField = new JTextField();
            jTextField.setComponentPopupMenu(popupMenu);
            return jTextField;
        });
        contentPanel.add(nameLine.getLeft(), "cell 0 0");
        contentPanel.add(nameLine.getRight(), "cell 1 0");

        packageLine = createLabelComponent("app.menu.file.new.package.label", () -> {
            JComboBox<String> comboBox = new JComboBox();
            comboBox.setEditable(true);
            comboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "com.myapp",
                    "com.myapp.core",
                    "com.myapp.ui",
                    "com.myapp.util",
                    "com.myapp.extras",
                    "com.myapp.components",
                    "com.myapp.dialogs",
                    "com.myapp.windows"
            }));
            return comboBox;
        });

        contentPanel.add(packageLine.getLeft(), "cell 0 1");
        contentPanel.add(packageLine.getRight(), "cell 1 1");

        typeLine = createLabelComponent("app.menu.file.new.type.label", () -> {
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                    "Class",
                    "Interface",
                    "Package",
                    "Annotation",
                    "Enum",
                    "Record",
                    "Java Project",
                    "Project",
                    "Folder",
                    "File"
            }));
            return comboBox;
        });
        contentPanel.add(typeLine.getLeft(), "cell 0 2");
        contentPanel.add(typeLine.getRight(), "cell 1 2");

        buttonBar.setLayout(new MigLayout(
                "insets dialog,alignx right",
                // columns
                "[button,fill]" +
                        "[button,fill]",
                // rows
                null));

        //---- okButton ----
        okButton.setText("OK");
        okButton.addActionListener(e -> okActionPerformed());
        buttonBar.add(okButton, "cell 0 0");

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(e -> cancelActionPerformed());
        buttonBar.add(cancelButton, "cell 1 0");
        dialogPane.add(contentPanel, BorderLayout.CENTER);
        dialogPane.add(buttonBar, BorderLayout.SOUTH);
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
    }

    private JPopupMenu createJPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        //---- cutMenuItem ----
        JMenuItem cutMenuItem = new JMenuItem();
        cutMenuItem.setText(I18nHelper.getMessage("app.menu.file.new.cut"));
        cutMenuItem.setMnemonic('C');
        popupMenu.add(cutMenuItem);
        //---- copyMenuItem ----
        JMenuItem copyMenuItem = new JMenuItem();
        copyMenuItem.setText(I18nHelper.getMessage("app.menu.file.new.copy"));
        copyMenuItem.setMnemonic('O');
        popupMenu.add(copyMenuItem);

        JMenuItem pasteMenuItem = new JMenuItem();
        //---- pasteMenuItem ----
        pasteMenuItem.setText(I18nHelper.getMessage("app.menu.file.new.paste"));
        pasteMenuItem.setMnemonic('P');
        popupMenu.add(pasteMenuItem);
        return popupMenu;
    }

    private <T> Pair<JLabel, T> createLabelComponent(String labelKey, Callable<T> createComponent) {
        String message = I18nHelper.getMessage(labelKey);
        try {
            return Pair.of(new JLabel(message), createComponent.call());
        } catch (Exception e) {
            log.error("create label component error", e);
            throw new RuntimeException(e);
        }
    }
}
