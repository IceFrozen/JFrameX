package cn.ximuli.jframex.ui.component.themes;

import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.storage.JFramePref;
import cn.ximuli.jframex.ui.manager.ThemeUIManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
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
public class IJThemesPanel extends JPanel {
    private final IJThemesManager themesManager = new IJThemesManager();
    private final List<IJThemeInfo> themes = new ArrayList<>();
    private final HashMap<Integer, String> categories = new HashMap<>();
    private final WindowListener windowListener = new WindowAdapter() {
        @Override
        public void windowActivated(WindowEvent e) {
            IJThemesPanel.this.windowActivated();
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

    public IJThemesPanel(ResourceLoaderManager resources) {
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
                String name = ((IJThemeInfo) value).name;
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
                if (ti.themeFile != null)
                    return ti.themeFile.getPath();
                if (ti.license == null)
                    return ti.name;

                return "Name: " + ti.name
                        + "\nLicense: " + ti.license
                        + "\nSource Code: " + ti.sourceCodeUrl;
            }
        });

        updateThemesList();
    }

    private void updateThemesList() {
        int filterLightDark = filterComboBox.getSelectedIndex();
        boolean showLight = (filterLightDark != 2);
        boolean showDark = (filterLightDark != 1);

        // load theme infos
        themesManager.loadBundledThemes();
        themesManager.loadThemesFromDirectory();

        // sort themes by name
        Comparator<? super IJThemeInfo> comparator = (t1, t2) -> t1.name.compareToIgnoreCase(t2.name);
        themesManager.bundledThemes.sort(comparator);
        themesManager.moreThemes.sort(comparator);

        // remember selection (must be invoked before clearing themes field)
        IJThemeInfo oldSel = themesList.getSelectedValue();

        themes.clear();
        categories.clear();

        // add core themes at beginning
        categories.put(themes.size(), "Core Themes");
        if (showLight)
            themes.add(new IJThemeInfo("FlatLaf Light", false, FlatLightLaf.class.getName()));
        if (showDark)
            themes.add(new IJThemeInfo("FlatLaf Dark", true, FlatDarkLaf.class.getName()));
        if (showLight)
            themes.add(new IJThemeInfo("FlatLaf IntelliJ", false, FlatIntelliJLaf.class.getName()));
        if (showDark)
            themes.add(new IJThemeInfo("FlatLaf Darcula", true, FlatDarculaLaf.class.getName()));

        if (showLight)
            themes.add(new IJThemeInfo("FlatLaf macOS Light", false, FlatMacLightLaf.class.getName()));
        if (showDark)
            themes.add(new IJThemeInfo("FlatLaf macOS Dark", true, FlatMacDarkLaf.class.getName()));

        // add themes from directory
        categories.put(themes.size(), "Current Directory");
        themes.addAll(themesManager.moreThemes);

        // add uncategorized bundled themes
        categories.put(themes.size(), "IntelliJ Themes");
        for (IJThemeInfo ti : themesManager.bundledThemes) {
            boolean show = (showLight && !ti.dark) || (showDark && ti.dark);
            if (show && !ti.name.contains("/"))
                themes.add(ti);
        }

        // add categorized bundled themes
        String lastCategory = null;
        for (IJThemeInfo ti : themesManager.bundledThemes) {
            boolean show = (showLight && !ti.dark) || (showDark && ti.dark);
            int sep = ti.name.indexOf('/');
            if (!show || sep < 0)
                continue;

            String category = ti.name.substring(0, sep).trim();
            if (!Objects.equals(lastCategory, category)) {
                lastCategory = category;
                categories.put(themes.size(), category);
            }

            themes.add(ti);
        }

        // fill themes list
        themesList.setModel(new AbstractListModel<IJThemeInfo>() {
            @Override
            public int getSize() {
                return themes.size();
            }

            @Override
            public IJThemeInfo getElementAt(int index) {
                return themes.get(index);
            }
        });

        // restore selection
        if (oldSel != null) {
            for (int i = 0; i < themes.size(); i++) {
                IJThemeInfo theme = themes.get(i);
                if (oldSel.name.equals(theme.name) &&
                        Objects.equals(oldSel.themeFile, theme.themeFile) &&
                        Objects.equals(oldSel.lafClassName, theme.lafClassName)) {
                    themesList.setSelectedIndex(i);
                    break;
                }
            }

            // select first theme if none selected
            if (themesList.getSelectedIndex() < 0)
                themesList.setSelectedIndex(0);
        }

        // scroll selection into visible area
        int sel = themesList.getSelectedIndex();
        if (sel >= 0) {
            Rectangle bounds = themesList.getCellBounds(sel, sel);
            if (bounds != null)
                themesList.scrollRectToVisible(bounds);
        }
    }

    public void selectPreviousTheme() {
        int sel = themesList.getSelectedIndex();
        if (sel > 0)
            themesList.setSelectedIndex(sel - 1);
    }

    public void selectNextTheme() {
        int sel = themesList.getSelectedIndex();
        themesList.setSelectedIndex(sel + 1);
    }

    private void themesListValueChanged(ListSelectionEvent e) {
        IJThemeInfo themeInfo = themesList.getSelectedValue();
        pluginButton.setEnabled(themeInfo != null && themeInfo.pluginUrl != null);
        sourceCodeButton.setEnabled(themeInfo != null && themeInfo.sourceCodePath != null);

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
        if (themeInfo == null || themeInfo.pluginUrl == null)
            return;

        browse(themeInfo.pluginUrl);
    }

    private void browseSourceCode() {
        IJThemeInfo themeInfo = themesList.getSelectedValue();
        if (themeInfo == null || themeInfo.sourceCodeUrl == null)
            return;

        String themeUrl = themeInfo.sourceCodeUrl;
        if (themeInfo.sourceCodePath != null)
            themeUrl += '/' + themeInfo.sourceCodePath;
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

    private void selectedCurrentLookAndFeel() {
        Predicate<IJThemeInfo> test;
        String lafClassName = UIManager.getLookAndFeel().getClass().getName();
        if (FlatPropertiesLaf.class.getName().equals(lafClassName) ||
                IntelliJTheme.ThemeLaf.class.getName().equals(lafClassName)) {
            String themeFileName = JFramePref.state.get(ThemeUIManager.KEY_LAF_THEME_FILE, "");
            if (themeFileName == null)
                return;

            File themeFile = new File(themeFileName);
            test = ti -> Objects.equals(ti.themeFile, themeFile);
        } else
            test = ti -> Objects.equals(ti.lafClassName, lafClassName);

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
        updateThemesList();
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
