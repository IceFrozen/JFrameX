package cn.ximuli.jframex.ui.component;

import cn.ximuli.jframex.common.exception.CommonException;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.model.Page;
import cn.ximuli.jframex.ui.I18nHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
@Getter
public class TablePanel<T> extends JPanel {
    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField pageTextField;
    private JButton goToPageButton;
    private JButton prePage;
    private JButton nextPage;
    private JLabel pageTotalLabel;

    private transient BiFunction<String, Page<T>, Page<T>> fetchAction;
    private final Map<Field, String> columnMaps = new LinkedHashMap<>();
    private final Map<Class<?>, BiFunction<T, Object, String>> converterMaps = new HashMap<>();

    private final Class<T> dataClass;
    private transient Page<T> page;

    public TablePanel(Class<T> t) {
        dataClass = t;
        setLayout(new BorderLayout());
        JPanel topPanel = initTopPanel();
        add(topPanel, BorderLayout.NORTH);
        JPanel bottomPanel = initBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public TablePanel<T> addFetchAction(BiFunction<String, Page<T>, Page<T>> fetchAction) {
        this.fetchAction = fetchAction;
        return this;
    }

    public TablePanel<T> addConvert(Class<?> classType, BiFunction<T, Object, String> convert) {
        this.converterMaps.put(classType, convert);
        return this;
    }

    private JPanel initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String searchLabelStr = I18nHelper.getMessage("app.message.search.label");
        searchLabel = new JLabel(searchLabelStr + ":");
        searchField = new JTextField(15);
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        searchButton = new JButton(I18nHelper.getMessage("app.message.search.button"));
        topPanel.add(searchButton);
        return topPanel;
    }

    private void initTable() {
        Field[] declaredFields = dataClass.getDeclaredFields();
        tableModel = new DefaultTableModel();
        String messagePrefix = "app.message.table." + dataClass.getSimpleName().toLowerCase() + ".";
        for (Field declaredField : declaredFields) {
            String messageCode = messagePrefix + declaredField.getName();
            if (I18nHelper.has(messageCode)) {
                columnMaps.put(declaredField, I18nHelper.getMessage(messageCode));
                tableModel.addColumn(I18nHelper.getMessage(messageCode));
            }
        }
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }


    public void renderData(Page<T> page) {
        if (page == null) {
            return;
        }
        this.page = page;
        List<T> data = page.getData();
        List<Object[]> values = new ArrayList<>();
        for (T datum : data) {
            Field[] declaredFields = datum.getClass().getDeclaredFields();
            Object[] array = Arrays.stream(declaredFields)
                    .filter(columnMaps::containsKey)
                    .filter(Objects::nonNull)
                    .map(f -> {
                        try {
                            f.setAccessible(true);
                            Class<?> fieldsClass = f.getType();
                            BiFunction<T, Object, String> converter = converterMaps.get(fieldsClass);
                            Object value = f.get(datum);
                            if (converter != null) {
                                return converter.apply(datum, value);
                            }
                            return f.get(datum);
                        } catch (IllegalAccessException e) {
                            throw new CommonException(e);
                        }
                    }).toArray();
            values.add(array);
        }
        fillTableData(values);
    }

    public void fillTableData(List<Object[]> rows) {
        this.tableModel.setRowCount(0);
        SwingUtilities.invokeLater(() -> {
            for (Object[] row : rows) {
                this.tableModel.addRow(row);
            }
            this.pageTotalLabel.setText(String.valueOf(this.page.getTotal()));
            updatePage();
        });
    }

    private void updatePage() {
        this.prePage.setEnabled(!page.isFirstPage());
        this.nextPage.setEnabled(!page.isLastPage());
        this.pageTotalLabel.setText(I18nHelper.getMessage("app.message.totalPage", this.page.getPage(), this.page.getTotalPage()));
        this.pageTextField.setEnabled(page.getTotalPage() > 1);
        this.pageTextField.setText(String.valueOf(this.page.getPage()));
        this.goToPageButton.setEnabled(page.getTotalPage() > 1);
    }

    private JPanel initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pageTotalLabel = new JLabel(I18nHelper.getMessage("app.message.totalPage", "0", "0"));
        pageTextField = new JTextField(5);
        pageTextField.setEnabled(false);
        goToPageButton = new JButton(I18nHelper.getMessage("app.message.toPage"));
        goToPageButton.setEnabled(false);
        prePage = new JButton(I18nHelper.getMessage("app.message.prePage"));
        prePage.setEnabled(false);
        nextPage = new JButton(I18nHelper.getMessage("app.message.nextPage"));
        nextPage.setEnabled(false);
        bottomPanel.add(prePage);
        bottomPanel.add(pageTextField);
        bottomPanel.add(pageTotalLabel);
        bottomPanel.add(goToPageButton);
        bottomPanel.add(nextPage);

        pageTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String pageText = pageTextField.getText();
                try {
                    int i = Integer.parseInt(pageText);

                    if (i <= 0) {
                        pageTextField.setText("1");
                        page.setPage(1);
                        return;
                    }
                    if (i > page.getTotalPage()) {
                        pageTextField.setText(String.valueOf(page.getTotal()));
                        page.setPage(page.getTotalPage());
                        return;
                    }
                    page.setPage(i);
                } catch (NumberFormatException ex) {
                    showMessageDialog("app.message.error.illegal.pageNum", pageText);
                }
            }
        });

        searchButton.addActionListener(e -> executeWithLoading(() -> {
            try {
                Page<T> currentPage = getPage();
                Thread.sleep(5000); // 模拟加载时间
                Page<T> newPage = fetchAction.apply(searchField.getText(), currentPage);
                renderData(newPage);
            } catch (Exception ex) {
                log.error("Error during search", ex);
                showMessageDialog("app.message.error", ex.getMessage());
            }
        }));

        goToPageButton.addActionListener(e -> executeWithLoading(() -> {
            String pageText = pageTextField.getText();
            try {
                int targetPage = Integer.parseInt(pageText); // 重命名局部变量
                Page<T> currentPage = this.getPage();
                if (targetPage > currentPage.getTotalPage()) {
                    showMessageDialog("app.message.error.illegal.pageNum", pageText);
                    return;
                }
                currentPage.setPage(targetPage); // 使用重命名后的变量
                Page<T> newPage = fetchAction.apply(searchField.getText(), currentPage);
                this.renderData(newPage);
            } catch (NumberFormatException ex) {
                showMessageDialog("app.message.error.illegal.pageNum", pageText);
            }
        }));

        nextPage.addActionListener(e -> executeWithLoading(() -> {
            Page<T> currentPage = this.page;
            currentPage.next();
            Page<T> newPage = fetchAction.apply(searchField.getText(), currentPage);
            this.renderData(newPage);
        }));

        prePage.addActionListener(e -> executeWithLoading(() -> {
            Page<T> currentPage = this.page;
            currentPage.pre();
            Page<T> newPage = fetchAction.apply(searchField.getText(), currentPage);
            this.renderData(newPage);
        }));

        return bottomPanel;
    }

    private void executeWithLoading(Runnable task) {
        enableAllButton(false);
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    task.run();
                } catch (Exception ex) {
                    log.error("Error during background task execution", ex);
                    showMessageDialog("app.message.error", ex.getMessage());
                }
                return null;
            }
            @Override
            protected void done() {
                enableAllButton(true);
            }
        };
        worker.execute();
    }

    public void enableAllButton(boolean enable) {
        Component[] components = this.getComponents();
        for (Component component : components) {
            if (component instanceof AbstractButton abstractButton) {
                abstractButton.setEnabled(enable);
            }
        }

        for (Component component : components) {
            if (component instanceof Container container) {
                enableButtonsInContainer(container, enable);
            }
        }
    }

    private void enableButtonsInContainer(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            if (component instanceof AbstractButton abstractButton) {
                abstractButton.setEnabled(enable);
            } else if (component instanceof Container subContainer) {
                enableButtonsInContainer(subContainer, enable);
            }
        }
    }

    public void showMessageDialog(String messageCode, Object... args) {
        JOptionPane.showMessageDialog(this, I18nHelper.getMessage(messageCode, args),
                I18nHelper.getMessage("app.message.title.error"), JOptionPane.ERROR_MESSAGE);
    }

    public Page<T> getPage() {
        Page<T> currentPage = this.page;
        if (currentPage == null) {
            int visibleHeight = scrollPane.getViewport().getHeight();
            int rowHeight = table.getRowHeight();
            int pageSize = visibleHeight / rowHeight;
            currentPage = new Page<>();
            currentPage.setTotal(0);
            currentPage.setPage(1);
            currentPage.setPageSize(pageSize);
            this.page = currentPage;
        }
        return this.page;
    }


    public static class TablePanelBuilder<T> {
        private final Class<T> dataClass;
        private String searchLabelStr;
        private BiFunction<String, Page<T>, Page<T>> fetchAction;
        private final Map<Class<?>, BiFunction<T, Object, String>> converterMaps = new HashMap<>();

        public TablePanelBuilder(Class<T> dataClass) {
            this.dataClass = dataClass;
        }

        public TablePanelBuilder<T> searchLabelStr(String searchLabelStr) {
            this.searchLabelStr = searchLabelStr;
            return this;
        }

        public TablePanelBuilder<T> searchAction(BiFunction<String, Page<T>, Page<T>> fetchAction) {
            this.fetchAction = fetchAction;
            return this;
        }

        public static <T> TablePanelBuilder<T> newBuilder(Class<T> dataClass) {
            return new TablePanelBuilder<>(dataClass);
        }

        public <U> TablePanelBuilder<T> converter(Class<U> clazz, BiFunction<T, Object, String> converter) {
            converterMaps.put(clazz, converter);
            return this;
        }

        public TablePanel<T> build() {
            TablePanel<T> tablePanel = new TablePanel<>(dataClass);
            if (StringUtil.isNotBlank(searchLabelStr)) {
                tablePanel.getSearchLabel().setText(searchLabelStr);
            }
            converterMaps.forEach(tablePanel::addConvert);
            tablePanel.addFetchAction(this.fetchAction).initTable();
            return tablePanel;
        }
    }
}