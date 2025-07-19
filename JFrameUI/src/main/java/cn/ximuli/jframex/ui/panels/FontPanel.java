package cn.ximuli.jframex.ui.panels;

import cn.ximuli.jframex.ui.component.intellijthemes.IJThemeInfo;
import cn.ximuli.jframex.ui.component.intellijthemes.IJThemesManager;
import cn.ximuli.jframex.ui.component.intellijthemes.ListCellTitledBorder;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.theme.ThemeUIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.ui.FlatListUI;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
public class FontPanel extends JPanel {
    public static final String THEMES_PACKAGE = "/style/intellijthemes/themes/";

    private final IJThemesManager themesManager = new IJThemesManager();
    private final List<IJThemeInfo> themes = new ArrayList<>();
    private final HashMap<Integer, String> categories = new HashMap<>();
    private final WindowListener windowListener = new WindowAdapter() {
        @Override
        public void windowActivated(WindowEvent e) {
            FontPanel.this.windowActivated();
        }
    };
    private Window window;

    private boolean isAdjustingThemesList;
    private long lastLafChangeTime = System.currentTimeMillis();

    private ResourceLoaderManager resources;
    private JToolBar toolBar;
    private JButton pluginButton;
    private JButton sourceCodeButton;
    private JComboBox<String> filterComboBox;
    private JScrollPane themesScrollPane;
    private JList<IJThemeInfo> themesList;

    public FontPanel(ResourceLoaderManager resources) {
        this.resources = resources;
        initComponents();

        // create renderer
        themesList.setCellRenderer(new DefaultListCellRenderer() {
            private int index;
            private boolean isSelected;
            private int titleHeight;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                this.index = index;
                this.isSelected = isSelected;
                this.titleHeight = 0;

                String title = categories.get(index);
                String name = ((IJThemeInfo) value).getName();
                int sep = name.indexOf('/');
                if (sep >= 0)
                    name = name.substring(sep + 1).trim();

                JComponent c = (JComponent) super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                c.setToolTipText(buildToolTip((IJThemeInfo) value));
                if (title != null) {
                    Border titledBorder = new ListCellTitledBorder(themesList, title);
                    c.setBorder(new CompoundBorder(titledBorder, c.getBorder()));
                    titleHeight = titledBorder.getBorderInsets(c).top;
                }
                return c;
            }

            @Override
            public boolean isOpaque() {
                return !isSelectedTitle();
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (isSelectedTitle()) {
                    g.setColor(getBackground());
                    FlatListUI.paintCellSelection(themesList, g, index, 0, titleHeight, getWidth(), getHeight() - titleHeight);
                }

                super.paintComponent(g);
            }

            private boolean isSelectedTitle() {
                return titleHeight > 0 && isSelected && UIManager.getLookAndFeel() instanceof FlatLaf;
            }

            private String buildToolTip(IJThemeInfo ti) {
                if (ti.getThemeFile() != null)
                    return ti.getThemeFile().getPath();
                if (ti.getLicense() == null)
                    return ti.getName();

                return "Name: " + ti.getName()
                        + "\nLicense: " + ti.getLicense()
                        + "\nSource Code: " + ti.getSourceCodeUrl();
            }
        });


    }




    private void themesListValueChanged(ListSelectionEvent e) {
        IJThemeInfo themeInfo = themesList.getSelectedValue();
        pluginButton.setEnabled(themeInfo != null && themeInfo.getPluginUrl() != null);
        sourceCodeButton.setEnabled(themeInfo != null && themeInfo.getSourceCodePath() != null);

        if (e.getValueIsAdjusting() || isAdjustingThemesList)
            return;

        EventQueue.invokeLater(() -> {
            setTheme(themeInfo, false);
        });
    }

    private void setTheme(IJThemeInfo themeInfo, boolean reload) {
        try {
            ThemeUIManager.setTheme(themeInfo, reload);
        } catch (Exception e) {
            log.error("Set Theme error", e);
            showInformationDialog(e.getMessage());
        }
    }

    private void browsePlugin() {
        IJThemeInfo themeInfo = themesList.getSelectedValue();
        if (themeInfo == null || themeInfo.getPluginUrl() == null)
            return;

        browse(themeInfo.getPluginUrl());
    }

    private void browseSourceCode() {
        IJThemeInfo themeInfo = themesList.getSelectedValue();
        if (themeInfo == null || themeInfo.getSourceCodeUrl() == null)
            return;

        String themeUrl = themeInfo.getSourceCodeUrl();
        if (themeInfo.getSourceCodePath() != null)
            themeUrl += '/' + themeInfo.getSourceCodePath();
        browse(themeUrl);
    }

    private void browse(String url) {
        url = url.replace(" ", "%20");
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException ex) {
            showInformationDialog("Failed to browse '" + url + "'. \r\n" + ex.getMessage());
        }
    }

    private void showInformationDialog(String message) {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this),
                message + "\n\n",
                "Theme", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void addNotify() {
        super.addNotify();

        selectedCurrentLookAndFeel();
        ThemeUIManager.themeChangeListener(() -> {
            selectedCurrentLookAndFeel();
            lastLafChangeTime = System.currentTimeMillis();
        });

        window = SwingUtilities.windowForComponent(this);
        if (window != null)
            window.addWindowListener(windowListener);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (window != null) {
            window.removeWindowListener(windowListener);
            window = null;
        }
    }


    private void windowActivated() {
        // refresh themes list on window activation
        if (themesManager.hasThemesFromDirectoryChanged())
            updateThemesList();
        else {
            // check whether core .properties files of current Laf have changed
            // in development environment since last Laf change and reload theme
            LookAndFeel laf = UIManager.getLookAndFeel();
            if (laf instanceof FlatLaf) {
                List<Class<?>> lafClasses = new ArrayList<>();

                // same as in UIDefaultsLoader.getLafClassesForDefaultsLoading()
                for (Class<?> lafClass = laf.getClass();
                     FlatLaf.class.isAssignableFrom(lafClass);
                     lafClass = lafClass.getSuperclass()) {
                    lafClasses.add(0, lafClass);
                }

                // same as in IntelliJTheme.ThemeLaf.getLafClassesForDefaultsLoading()
                if (laf instanceof IntelliJTheme.ThemeLaf) {
                    boolean dark = ((FlatLaf) laf).isDark();
                    lafClasses.add(1, dark ? FlatDarkLaf.class : FlatLightLaf.class);
                    lafClasses.add(2, dark ? FlatDarculaLaf.class : FlatIntelliJLaf.class);
                }

                boolean reload = false;
                for (Class<?> lafClass : lafClasses) {
                    String propertiesName = '/' + lafClass.getName().replace('.', '/') + ".properties";
                    URL url = lafClass.getResource(propertiesName);
                    if (url != null && "file".equals(url.getProtocol())) {
                        try {
                            File file = new File(url.toURI());
                            if (file.lastModified() > lastLafChangeTime) {
                                reload = true;
                                break;
                            }
                        } catch (URISyntaxException ex) {
                            // ignore
                        }
                    }
                }

                if (reload)
                    setTheme(themesList.getSelectedValue(), true);
            }
        }
    }

    private void updateThemesList() {
    }

    private void selectedCurrentLookAndFeel() {
        Predicate<IJThemeInfo> test;
        String lafClassName = UIManager.getLookAndFeel().getClass().getName();
        if (FlatPropertiesLaf.class.getName().equals(lafClassName) ||
                IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
            String themeFileName = JFramePref.state.get(ThemeUIManager.KEY_LAF_THEME_FILE, "");
            if (themeFileName == null)
                return;

            File themeFile = new File(themeFileName);
            test = ti -> Objects.equals(ti.getThemeFile(), themeFile);
        } else
            test = ti -> Objects.equals(ti.getLafClassName(), lafClassName);

        int newSel = -1;
        for (int i = 0; i < themes.size(); i++) {
            if (test.test(themes.get(i))) {
                newSel = i;
                break;
            }
        }

        isAdjustingThemesList = true;
        if (newSel >= 0) {
            if (newSel != themesList.getSelectedIndex())
                themesList.setSelectedIndex(newSel);
        } else
            themesList.clearSelection();
        isAdjustingThemesList = false;
    }

    private void filterChanged() {

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        JLabel themesLabel = new JLabel();
        toolBar = new JToolBar();
        pluginButton = new JButton();
        sourceCodeButton = new JButton();
        filterComboBox = new JComboBox<>();
        themesScrollPane = new JScrollPane();
        themesList = new JList<>();

        //======== this ========
        setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[]3" +
                        "[grow,fill]"));

        //---- themesLabel ----
        themesLabel.setText("Themes:");
        add(themesLabel, "cell 0 0");

        //======== toolBar ========

        toolBar.setFloatable(false);

        //---- pluginButton ----
        pluginButton.setToolTipText("Opens the IntelliJ plugin page of selected IntelliJ theme in the browser.");

        pluginButton.setIcon(resources.getIcon("icons/plugin"));
        pluginButton.addActionListener(e -> browsePlugin());
        toolBar.add(pluginButton);

        //---- sourceCodeButton ----
        sourceCodeButton.setToolTipText("Opens the source code repository of selected IntelliJ theme in the browser.");
        sourceCodeButton.setIcon(resources.getIcon("icons/github"));
        sourceCodeButton.addActionListener(e -> browseSourceCode());
        toolBar.add(sourceCodeButton);

        add(toolBar, "cell 0 0,alignx right,growx 0");

        //---- filterComboBox ----
        filterComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "all",
                "light",
                "dark"
        }));
        filterComboBox.putClientProperty(FlatClientProperties.MINIMUM_WIDTH, 0);
        filterComboBox.setFocusable(false);
        filterComboBox.addActionListener(e -> filterChanged());
        add(filterComboBox, "cell 0 0,alignx right,growx 0");

        themesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        themesList.addListSelectionListener(e -> themesListValueChanged(e));
        themesScrollPane.setViewportView(themesList);

        add(themesScrollPane, "cell 0 1");

    }
}
