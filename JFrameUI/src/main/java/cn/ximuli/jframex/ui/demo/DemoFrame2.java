package cn.ximuli.jframex.ui.demo;

import cn.ximuli.jframex.ui.demo.intellijthemes.IJThemesPanel;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.*;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.Preferences;

public class DemoFrame2 extends JFrame {
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuBar menuBar;
    private JMenuItem exitMenuItem;
    private JMenu scrollingPopupMenu;
    private JMenuItem htmlMenuItem;
    private JMenu fontMenu;
    private JMenu optionsMenu;
    private JCheckBoxMenuItem windowDecorationsCheckBoxMenuItem;
    private JCheckBoxMenuItem menuBarEmbeddedCheckBoxMenuItem;
    private JCheckBoxMenuItem unifiedTitleBarMenuItem;
    private JCheckBoxMenuItem showTitleBarIconMenuItem;
    private JCheckBoxMenuItem underlineMenuSelectionMenuItem;
    private JCheckBoxMenuItem alwaysShowMnemonicsMenuItem;
    private JCheckBoxMenuItem animatedLafChangeMenuItem;
    private JMenuItem aboutMenuItem;
    private JToolBar toolBar;

    private final String[] availableFontFamilyNames;
    private int initialFontMenuItemCount = -1;

    DemoFrame2() {
        int tabIndex = DemoPrefs.getState().getInt(FlatLafDemo.KEY_TAB, 0);

        availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
        Arrays.sort(availableFontFamilyNames);

        initComponents();
        initAccentColors();



        // macOS  (see https://www.formdev.com/flatlaf/macos/)
        if (SystemInfo.isMacOS) {
            JRootPane rootPane = getRootPane();
            if (SystemInfo.isMacFullWindowContentSupported) {
                // expand window content into window title bar and make title bar transparent
                rootPane.putClientProperty("apple.awt.fullWindowContent", true);
                rootPane.putClientProperty("apple.awt.transparentTitleBar", true);
                rootPane.putClientProperty(FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING, FlatClientProperties.MACOS_WINDOW_BUTTONS_SPACING_LARGE);

                // hide window title
                if (SystemInfo.isJava_17_orLater)
                    rootPane.putClientProperty("apple.awt.windowTitleVisible", false);
                else
                    setTitle(null);
            }

            // enable full screen mode for this window (for Java 8 - 10; not necessary for Java 11+)
            if (!SystemInfo.isJava_11_orLater)
                rootPane.putClientProperty("apple.awt.fullscreenable", true);
        }



    }

    @Override
    public void dispose() {
        super.dispose();

        FlatUIDefaultsInspector.hide();
    }

    private void showHints() {
        HintManager.Hint fontMenuHint = new HintManager.Hint(
                "Use 'Font' menu to increase/decrease font size or try different fonts.",
                fontMenu, SwingConstants.BOTTOM, "hint.fontMenu", null);

        HintManager.Hint optionsMenuHint = new HintManager.Hint(
                "Use 'Options' menu to try out various FlatLaf options.",
                optionsMenu, SwingConstants.BOTTOM, "hint.optionsMenu", fontMenuHint);
    }

    private void clearHints() {
        HintManager.hideAllHints();

        Preferences state = DemoPrefs.getState();
        state.remove("hint.fontMenu");
        state.remove("hint.optionsMenu");
        state.remove("hint.themesPanel");
    }

    private void showUIDefaultsInspector() {
        FlatUIDefaultsInspector.show();
    }

    private void newActionPerformed() {
        NewDialog newDialog = new NewDialog(this);
        newDialog.setVisible(true);
    }

    private void openActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showOpenDialog(this);
    }

    private void saveAsActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog(this);
    }

    private void exitActionPerformed() {
        dispose();
    }

    private void aboutActionPerformed() {
        JLabel titleLabel = new JLabel("FlatLaf Demo");
        titleLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        String link = "https://www.formdev.com/flatlaf/";
        JLabel linkLabel = new JLabel("<html><a href=\"#\">" + link + "</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(link));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(linkLabel,
                            "Failed to open '" + link + "' in browser.",
                            "About", JOptionPane.PLAIN_MESSAGE);
                }
            }
        });


        JOptionPane.showMessageDialog(this,
                new Object[]{
                        titleLabel,
                        "Demonstrates FlatLaf Swing look and feel",
                        " ",
                        "Copyright 2019-" + Year.now() + " FormDev Software GmbH",
                        linkLabel,
                },
                "About", JOptionPane.PLAIN_MESSAGE);
    }

    private void showPreferences() {
        JOptionPane.showMessageDialog(this,
                "Sorry, but FlatLaf Demo does not have preferences. :(\n"
                        + "This dialog is here to demonstrate usage of class 'FlatDesktop' on macOS.",
                "Preferences", JOptionPane.PLAIN_MESSAGE);
    }



    private void menuItemActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, e.getActionCommand(), "Menu Item", JOptionPane.PLAIN_MESSAGE);
        });
    }

    private void windowDecorationsChanged() {
        boolean windowDecorations = windowDecorationsCheckBoxMenuItem.isSelected();

        if (SystemInfo.isLinux) {
            // enable/disable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(windowDecorations);
            JDialog.setDefaultLookAndFeelDecorated(windowDecorations);

            // dispose frame, update decoration and re-show frame
            dispose();
            setUndecorated(windowDecorations);
            getRootPane().setWindowDecorationStyle(windowDecorations ? JRootPane.FRAME : JRootPane.NONE);
            setVisible(true);
        } else {
            // change window decoration of all frames and dialogs
            FlatLaf.setUseNativeWindowDecorations(windowDecorations);
        }

        menuBarEmbeddedCheckBoxMenuItem.setEnabled(windowDecorations);
        unifiedTitleBarMenuItem.setEnabled(windowDecorations);
        showTitleBarIconMenuItem.setEnabled(windowDecorations);
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
        clearHints();
        showHints();
    }

    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = e.getActionCommand();

        FlatAnimatedLafChange.showSnapshot();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = FontUtils.getCompositeFont(fontFamily, font.getStyle(), font.getSize());
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private void fontSizeChanged(ActionEvent e) {
        String fontSizeStr = e.getActionCommand();

        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Integer.parseInt(fontSizeStr));
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
    }

    private void restoreFont() {
        UIManager.put("defaultFont", null);
        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void incrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) (font.getSize() + 1));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    private void decrFont() {
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) Math.max(font.getSize() - 1, 10));
        UIManager.put("defaultFont", newFont);

        updateFontMenuItems();
        FlatLaf.updateUI();
    }

    void updateFontMenuItems() {
        if (initialFontMenuItemCount < 0)
            initialFontMenuItemCount = fontMenu.getItemCount();
        else {
            // remove old font items
            for (int i = fontMenu.getItemCount() - 1; i >= initialFontMenuItemCount; i--)
                fontMenu.remove(i);
        }

        // get current font
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());

        // add font families
        fontMenu.addSeparator();
        ArrayList<String> families = new ArrayList<>(Arrays.asList(
                "Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans",
                "Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto",
                "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        if (!families.contains(currentFamily))
            families.add(currentFamily);
        families.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup familiesGroup = new ButtonGroup();
        for (String family : families) {
            if (Arrays.binarySearch(availableFontFamilyNames, family) < 0)
                continue; // not available

            JCheckBoxMenuItem item = new JCheckBoxMenuItem(family);
            item.setSelected(family.equals(currentFamily));
            item.addActionListener(this::fontFamilyChanged);
            fontMenu.add(item);

            familiesGroup.add(item);
        }

        // add font sizes
        fontMenu.addSeparator();
        ArrayList<String> sizes = new ArrayList<>(Arrays.asList(
                "10", "11", "12", "14", "16", "18", "20", "24", "28"));
        if (!sizes.contains(currentSize))
            sizes.add(currentSize);
        sizes.sort(String.CASE_INSENSITIVE_ORDER);

        ButtonGroup sizesGroup = new ButtonGroup();
        for (String size : sizes) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(size);
            item.setSelected(size.equals(currentSize));
            item.addActionListener(this::fontSizeChanged);
            fontMenu.add(item);

            sizesGroup.add(item);
        }

        // enabled/disable items
        boolean enabled = UIManager.getLookAndFeel() instanceof FlatLaf;
        for (Component item : fontMenu.getMenuComponents())
            item.setEnabled(enabled);
    }

    // the real colors are defined in
    // flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatLightLaf.properties and
    // flatlaf-demo/src/main/resources/com/formdev/flatlaf/demo/FlatDarkLaf.properties
    private static String[] accentColorKeys = {
            "Demo.accent.default", "Demo.accent.blue", "Demo.accent.purple", "Demo.accent.red",
            "Demo.accent.orange", "Demo.accent.yellow", "Demo.accent.green",
    };
    private static String[] accentColorNames = {
            "Default", "Blue", "Purple", "Red", "Orange", "Yellow", "Green",
    };
    private final JToggleButton[] accentColorButtons = new JToggleButton[accentColorKeys.length];
    private JLabel accentColorLabel;
    private Color accentColor;

    private void initAccentColors() {
        accentColorLabel = new JLabel("Accent color: ");

        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(accentColorLabel);

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < accentColorButtons.length; i++) {
            accentColorButtons[i] = new JToggleButton(new AccentColorIcon(accentColorKeys[i]));
            accentColorButtons[i].setToolTipText(accentColorNames[i]);
            accentColorButtons[i].addActionListener(this::accentColorChanged);
            toolBar.add(accentColorButtons[i]);
            group.add(accentColorButtons[i]);
        }

        accentColorButtons[0].setSelected(true);

        FlatLaf.setSystemColorGetter(name -> {
            return name.equals("accent") ? accentColor : null;
        });

        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName()))
                updateAccentColorButtons();
        });
        updateAccentColorButtons();
    }

    private void accentColorChanged(ActionEvent e) {
        String accentColorKey = null;
        for (int i = 0; i < accentColorButtons.length; i++) {
            if (accentColorButtons[i].isSelected()) {
                accentColorKey = accentColorKeys[i];
                break;
            }
        }

        accentColor = (accentColorKey != null && accentColorKey != accentColorKeys[0])
                ? UIManager.getColor(accentColorKey)
                : null;

        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        try {
            FlatLaf.setup(lafClass.getDeclaredConstructor().newInstance());
            FlatLaf.updateUI();
        } catch (Exception ex) {
            LoggingFacade.INSTANCE.logSevere(null, ex);
        }
    }

    private void updateAccentColorButtons() {
        Class<? extends LookAndFeel> lafClass = UIManager.getLookAndFeel().getClass();
        boolean isAccentColorSupported =
                lafClass == FlatLightLaf.class ||
                        lafClass == FlatDarkLaf.class ||
                        lafClass == FlatIntelliJLaf.class ||
                        lafClass == FlatDarculaLaf.class ||
                        lafClass == FlatMacLightLaf.class ||
                        lafClass == FlatMacDarkLaf.class;

        accentColorLabel.setVisible(isAccentColorSupported);
        for (int i = 0; i < accentColorButtons.length; i++)
            accentColorButtons[i].setVisible(isAccentColorSupported);
    }

    private void initFullWindowContent() {
        if (!supportsFlatLafWindowDecorations())
            return;

        // create fullWindowContent mode toggle button
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

    }

    private boolean supportsFlatLafWindowDecorations() {
        return FlatLaf.supportsNativeWindowDecorations() || SystemInfo.isLinux;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollingPopupMenu = new JMenu();

        fontMenu = new JMenu();

        JPanel toolBarPanel = new JPanel();
        JPanel macFullWindowContentButtonsPlaceholder = new JPanel();
        toolBar = new JToolBar();
        JButton backButton = new JButton();
        JButton forwardButton = new JButton();
        JButton cutButton = new JButton();
        JButton copyButton = new JButton();
        JButton pasteButton = new JButton();
        JButton refreshButton = new JButton();
        JToggleButton showToggleButton = new JToggleButton();
        JPanel contentPanel = new JPanel();



        JPanel winFullWindowContentButtonsPlaceholder = new JPanel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== toolBarPanel ========
        {
            toolBarPanel.setLayout(new BorderLayout());

            //======== macFullWindowContentButtonsPlaceholder ========
            {
                macFullWindowContentButtonsPlaceholder.setLayout(new FlowLayout());
            }
            toolBarPanel.add(macFullWindowContentButtonsPlaceholder, BorderLayout.WEST);

            //======== toolBar ========
            {
                toolBar.setMargin(new Insets(3, 3, 3, 3));

                //---- backButton ----
                backButton.setToolTipText("Back");
                backButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/back.svg"));
                toolBar.add(backButton);

                //---- forwardButton ----
                forwardButton.setToolTipText("Forward");
                forwardButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/forward.svg"));
                toolBar.add(forwardButton);
                toolBar.addSeparator();

                //---- cutButton ----
                cutButton.setToolTipText("Cut");
                cutButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-cut.svg"));
                toolBar.add(cutButton);

                //---- copyButton ----
                copyButton.setToolTipText("Copy");
                copyButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/copy.svg"));
                toolBar.add(copyButton);

                //---- pasteButton ----
                pasteButton.setToolTipText("Paste");
                pasteButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/menu-paste.svg"));
                toolBar.add(pasteButton);
                toolBar.addSeparator();

                //---- refreshButton ----
                refreshButton.setToolTipText("Refresh");
                refreshButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/refresh.svg"));
                toolBar.add(refreshButton);
                toolBar.addSeparator();

                //---- showToggleButton ----
                showToggleButton.setSelected(true);
                showToggleButton.setToolTipText("Show Details");
                showToggleButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/show.svg"));
                toolBar.add(showToggleButton);
            }
            toolBarPanel.add(toolBar, BorderLayout.CENTER);
        }
        contentPane.add(toolBarPanel, BorderLayout.PAGE_START);

        //======== contentPanel ========
        {
            contentPanel.setLayout(new MigLayout(
                    "insets dialog,hidemode 3",
                    // columns
                    "[grow,fill]",
                    // rows
                    "[grow,fill]"));


        }
        contentPane.add(contentPanel, BorderLayout.CENTER);


        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // add "Users" button to menubar
        FlatButton usersButton = new FlatButton();
        usersButton.setIcon(new FlatSVGIcon("com/formdev/flatlaf/demo/icons/users.svg"));
        usersButton.setButtonType(FlatButton.ButtonType.toolBarButton);
        usersButton.setFocusable(false);
        usersButton.addActionListener(e -> JOptionPane.showMessageDialog(null, "Hello User! How are you?", "User", JOptionPane.INFORMATION_MESSAGE));
//        menuBar.add(Box.createGlue());
//        menuBar.add(usersButton);



        for (int i = 1; i <= 100; i++)
            scrollingPopupMenu.add("Item " + i);

        if (supportsFlatLafWindowDecorations()) {
            windowDecorationsCheckBoxMenuItem.setSelected(SystemInfo.isLinux
                    ? JFrame.isDefaultLookAndFeelDecorated()
                    : FlatLaf.isUseNativeWindowDecorations());
            menuBarEmbeddedCheckBoxMenuItem.setSelected(UIManager.getBoolean("TitlePane.menuBarEmbedded"));
            unifiedTitleBarMenuItem.setSelected(UIManager.getBoolean("TitlePane.unifiedBackground"));
            showTitleBarIconMenuItem.setSelected(UIManager.getBoolean("TitlePane.showIcon"));
        }



        if ("false".equals(System.getProperty("flatlaf.animatedLafChange")))
            animatedLafChangeMenuItem.setSelected(false);


        // on macOS, panel left to toolBar is a placeholder for title bar buttons in fullWindowContent mode
        macFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "mac zeroInFullScreen");

        // on Windows/Linux, panel above themesPanel is a placeholder for title bar buttons in fullWindowContent mode
        winFullWindowContentButtonsPlaceholder.putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER, "win");

        // uncomment this line to see title bar buttons placeholders in fullWindowContent mode
		//UIManager.put( "FlatLaf.debug.panel.showPlaceholders", true );


        // remove contentPanel bottom insets
//        MigLayout layout = (MigLayout) contentPanel.getLayout();
//        LC lc = ConstraintParser.parseLayoutConstraint((String) layout.getLayoutConstraints());
//        UnitValue[] insets = lc.getInsets();
//        lc.setInsets(new UnitValue[]{
//                insets[0],
//                insets[1],
//                new UnitValue(0, UnitValue.PIXEL, null),
//                insets[3]
//        });
//        layout.setLayoutConstraints(lc);
    }


    // JFormDesigner - End of variables declaration  //GEN-END:variables

    //---- class AccentColorIcon ----------------------------------------------

    private static class AccentColorIcon
            extends FlatAbstractIcon {
        private final String colorKey;

        AccentColorIcon(String colorKey) {
            super(16, 16, null);
            this.colorKey = colorKey;
        }

        @Override
        protected void paintIcon(Component c, Graphics2D g) {
            Color color = UIManager.getColor(colorKey);
            if (color == null)
                color = Color.lightGray;
            else if (!c.isEnabled()) {
                color = FlatLaf.isLafDark()
                        ? ColorFunctions.shade(color, 0.5f)
                        : ColorFunctions.tint(color, 0.6f);
            }

            g.setColor(color);
            g.fillRoundRect(1, 1, width - 2, height - 2, 5, 5);
        }
    }
}
