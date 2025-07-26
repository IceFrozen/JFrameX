package cn.ximuli.jframex.ui.component.panels;

import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.service.util.SpringUtils;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.component.themes.ListCellTitledBorder;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.formdev.flatlaf.ui.FlatListUI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.annotation.AnnotationUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@org.springframework.stereotype.Component
@Slf4j
public class SettingListPanel extends JPanel {

    private Map<Integer, String> categories;
    private ResourceLoaderManager resources;

    private JScrollPane settingScrollPane;
    private JList<SettingInfo<JComponent>> settingsList;
    private List<SettingInfo<JComponent>> settingInfos;
    private Consumer<SettingInfo<JComponent>> selectAction;

    private List<JComponent> settingPanels;

    @Getter
    private volatile SettingInfo<JComponent> currenSettings;

    private volatile AtomicBoolean isChangeUI = new AtomicBoolean(false);
    @Getter
    JTextField searchTextField;


    public SettingListPanel(ResourceLoaderManager resources) {
        this.resources = resources;
        searchTextField = new JTextField();
        initSettingLists();
        initComponents();

        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePanels();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePanels();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePanels();
            }
        });

        settingsList.setCellRenderer(new DefaultListCellRenderer() {
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
                String name = ((SettingInfo<?>) value).getName();
                int sep = name.indexOf('/');
                if (sep >= 0)
                    name = name.substring(sep + 1).trim();

                JComponent c = (JComponent) super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                c.setToolTipText(buildToolTip((SettingInfo<?>) value));
                if (title != null) {
                    Border titledBorder = new ListCellTitledBorder(settingsList, title);
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
                    FlatListUI.paintCellSelection(settingsList, g, index, 0, titleHeight, getWidth(), getHeight() - titleHeight);
                }

                super.paintComponent(g);
            }

            private boolean isSelectedTitle() {
                return titleHeight > 0 && isSelected;
            }

            private String buildToolTip(SettingInfo sInfo) {
                return sInfo.getToolTipText();
            }
        });
    }

    private void initSettingLists() {
        this.categories = new HashMap<>();
        this.settingInfos = new ArrayList<>();
        this.settingsList = new JList<>();
        this.settingPanels = SpringUtils.getBean(JComponent.class, SettingMenu.class);
        this.isChangeUI.set(true);
        updatePanels();
        this.isChangeUI.set(false);
    }

    private void listValueChanged(ListSelectionEvent e) {
        SettingInfo<JComponent> settingInfo = settingsList.getSelectedValue();
        if (e.getValueIsAdjusting() || isChangeUI.get() || currenSettings == settingInfo) {
            return;
        }

        if (this.selectAction != null && settingInfo != null) {
            this.selectAction.accept(settingInfo);
            this.currenSettings = settingInfo;
        }
    }

    public void addSelectedAction(Consumer<SettingInfo<JComponent>> action) {
        this.selectAction = action;
    }

    private void initComponents() {
        settingScrollPane = new JScrollPane();
        setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                // columns
                "[grow,fill]",
                // rows
                "[]3" +
                        "[grow,fill]"));

        searchTextField = new JTextField();
        // search history button
        JButton searchHistoryButton = new JButton(new FlatSearchWithHistoryIcon(true));
        searchHistoryButton.setToolTipText(I18nHelper.getMessage("app.setting.search.history.toolTipText"));
        searchHistoryButton.addActionListener(e -> {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("(empty)");
            popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });
        searchTextField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchHistoryButton);
        add(searchTextField, "cell 0 0");

        settingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        settingsList.addListSelectionListener(this::listValueChanged);
        settingScrollPane.setViewportView(settingsList);
        add(settingScrollPane, "cell 0 1");
    }

    private int getMaxContentWidth() {
        int maxWidth = 0;
        Font font = settingsList.getFont();
        if (font == null) {
            font = UIManager.getFont("List.font");
        }
        FontMetrics fm = settingsList.getFontMetrics(font);
        for (SettingInfo<JComponent> item : settingInfos) {
            String name = item.getName();
            int sep = name.indexOf('/');
            if (sep >= 0) {
                name = name.substring(sep + 1).trim();
            }
            int textWidth = fm.stringWidth(name);
            maxWidth = Math.max(maxWidth, textWidth);
        }
        return maxWidth;
    }

    public void select(int index) {
        settingsList.setSelectedIndex(index);
    }


    public List<JComponent> filterSettingPanels() {
        String searchKeyWorlds = searchTextField.getText();
        if (StringUtil.isBlank(searchKeyWorlds)) {
            return settingPanels;
        }
        return settingPanels.stream()
                .filter(settingPanel -> {
                    SettingMenu settingMenu = AnnotationUtils.getAnnotation(settingPanel.getClass(), SettingMenu.class);
                    return StringUtil.containsIgnoreCase(I18nHelper.getMessage(settingMenu.value()), searchKeyWorlds);
                }).toList();
    }

    private void updatePanels() {
        this.categories.clear();
        this.settingInfos.clear();

        LinkedHashMap<String, List<Pair<SettingMenu, JComponent>>> settingCategory = new LinkedHashMap<>();
        List<JComponent> jComponents = filterSettingPanels();
        if (!jComponents.isEmpty()) {
            for (JComponent settingPanel : jComponents) {
                SettingMenu settingMenu = AnnotationUtils.getAnnotation(settingPanel.getClass(), SettingMenu.class);
                List<Pair<SettingMenu, JComponent>> jPanels = settingCategory.getOrDefault(settingMenu.category(), new ArrayList<>());
                jPanels.add(Pair.of(settingMenu, settingPanel));
                settingCategory.put(settingMenu.category(), jPanels);
            }

            int index = 0;
            for (Map.Entry<String, List<Pair<SettingMenu, JComponent>>> settings : settingCategory.entrySet()) {
                String categoryName = I18nHelper.has(settings.getKey()) ? I18nHelper.getMessage(settings.getKey()) : settings.getKey();
                categories.put(index, categoryName);
                settings.getValue().sort(Comparator.comparingInt(pair -> -pair.getLeft().order()));
                for (Pair<SettingMenu, JComponent> pair : settings.getValue()) {
                    SettingMenu settingMeta = pair.getKey();
                    JComponent settingClass = pair.getValue();
                    String settingName = I18nHelper.has(settingMeta.value()) ? I18nHelper.getMessage(settingMeta.value()) : settingMeta.value();
                    String settingTooltipText = I18nHelper.has(settingMeta.toolTipText()) ? I18nHelper.getMessage(settingMeta.toolTipText()) : settingMeta.toolTipText();
                    SettingInfo<JComponent> info = new SettingInfo(categoryName, settingName, settingTooltipText, settingClass.getClass());
                    settingInfos.add(info);
                }
                index = index + settings.getValue().size();
            }
        }

        settingsList.setModel(new AbstractListModel<>() {
            @Override
            public int getSize() {
                return settingInfos.size();
            }

            @Override
            public SettingInfo<JComponent> getElementAt(int index) {
                return settingInfos.get(index);
            }
        });


        int maxWidth = getMaxContentWidth();
        settingsList.setFixedCellWidth(maxWidth * 3);


        // scroll selection into visible area
        int sel = settingsList.getSelectedIndex();
        if (sel >= 0) {
            Rectangle bounds = settingsList.getCellBounds(sel, sel);
            if (bounds != null) {
                settingsList.scrollRectToVisible(bounds);
            }
        }
    }
}
