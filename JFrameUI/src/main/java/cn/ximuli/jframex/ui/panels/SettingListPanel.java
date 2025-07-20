package cn.ximuli.jframex.ui.panels;

import cn.ximuli.jframex.ui.component.intellijthemes.IJThemeInfo;
import cn.ximuli.jframex.ui.component.intellijthemes.ListCellTitledBorder;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.formdev.flatlaf.ui.FlatListUI;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class SettingListPanel extends JPanel {
    private Map<Integer, String> categories;
    private ResourceLoaderManager resources;

    private JScrollPane settingScrollPane;
    private JList<SettingInfo> settingsList;
    private List<SettingInfo> settingInfos;
    private Consumer<SettingInfo> selectAction;

    private volatile SettingInfo currenSettings;

    private volatile AtomicBoolean isChangeUI = new AtomicBoolean(false);
    @Getter
    JTextField searchTextField;


    public SettingListPanel(ResourceLoaderManager resources) {
        this.resources = resources;
        initSettingLists();
        initComponents();
        // create renderer
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
                String name = ((SettingInfo) value).getName();
                int sep = name.indexOf('/');
                if (sep >= 0)
                    name = name.substring(sep + 1).trim();

                JComponent c = (JComponent) super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);
                c.setToolTipText(buildToolTip((SettingInfo) value));
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

        categories.put(settingInfos.size(), "Demo Setting");
        SettingInfo demoSettingInfo = new SettingInfo();
        demoSettingInfo.setName("Demo");
        demoSettingInfo.setCategory("cate1");
        SettingInfo otherSettingInfo = new SettingInfo();
        otherSettingInfo.setName("Other");
        otherSettingInfo.setCategory("cate2");

        settingInfos.add(demoSettingInfo);
        settingInfos.add(otherSettingInfo);

        categories.put(settingInfos.size(), "Demo Setting2");

        for (int i = 0; i < 100; i++) {
            SettingInfo demoSettingInfoItem = new SettingInfo();
            demoSettingInfoItem.setName("Demo" + i);
            demoSettingInfoItem.setCategory("cate3");
            settingInfos.add(demoSettingInfoItem);
        }

        settingsList.setModel(new AbstractListModel<SettingInfo>() {
            @Override
            public int getSize() {
                return settingInfos.size();
            }
            @Override
            public SettingInfo getElementAt(int index) {
                return settingInfos.get(index);
            }
        });


        int maxWidth = getMaxContentWidth();
        settingsList.setFixedCellWidth(maxWidth * 3);

        if (settingsList.getSelectedIndex() < 0) {
            settingsList.setSelectedIndex(0);
        }

        // scroll selection into visible area
        int sel = settingsList.getSelectedIndex();
        if (sel >= 0) {
            Rectangle bounds = settingsList.getCellBounds(sel, sel);
            if (bounds != null) {
                settingsList.scrollRectToVisible(bounds);
            }
        }
    }

    private void listValueChanged(ListSelectionEvent e) {
        SettingInfo settingInfo = settingsList.getSelectedValue();
        if (e.getValueIsAdjusting() || isChangeUI.get() || currenSettings == settingInfo) {
            return;
        }

        if (this.selectAction != null) {
            this.selectAction.accept(settingInfo);
            this.currenSettings = settingInfo;
        }
    }

    public SettingListPanel addSelectedAction(Consumer<SettingInfo> action) {
        this.selectAction = action;
        return this;
    }

    private void initComponents() {
        settingScrollPane = new JScrollPane();
        //======== this ========
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
        searchHistoryButton.setToolTipText("Search History");
        searchHistoryButton.addActionListener(e -> {
            JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.add("(empty)");
            popupMenu.show(searchHistoryButton, 0, searchHistoryButton.getHeight());
        });
        searchTextField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, searchHistoryButton);
        add(searchTextField, "cell 0 0");

        settingsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        settingsList.addListSelectionListener(e -> listValueChanged(e));
        settingScrollPane.setViewportView(settingsList);

        add(settingScrollPane, "cell 0 1");


    }
    // 计算最大字符串宽度
    private int getMaxContentWidth() {
        int maxWidth = 0;
        Font font = settingsList.getFont();
        if (font == null) {
            font = UIManager.getFont("List.font");
        }
        FontMetrics fm = settingsList.getFontMetrics(font);
        for (SettingInfo item : settingInfos) {
            String name = item.getName();
            int sep = name.indexOf('/');
            if (sep >= 0) {
                name = name.substring(sep + 1).trim(); // 与渲染器中处理一致
            }
            int textWidth = fm.stringWidth(name);
            maxWidth = Math.max(maxWidth, textWidth);
        }
        return maxWidth;
    }
}
