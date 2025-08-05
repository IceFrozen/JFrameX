package cn.ximuli.jframex.ui.component.panels;

import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;

import cn.ximuli.jframex.ui.MainFrame;
import cn.ximuli.jframex.ui.component.Hintable;
import cn.ximuli.jframex.ui.event.RestartEvent;
import cn.ximuli.jframex.ui.manager.FrameManager;
import cn.ximuli.jframex.ui.manager.HintManager;
import cn.ximuli.jframex.ui.manager.ResourceLoaderManager;
import cn.ximuli.jframex.ui.manager.ThemeUIManager;
import cn.ximuli.jframex.ui.storage.JFramePref;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.Ordered;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;


@Slf4j
@SettingMenu(value = "app.setting.item.components.font", category = "app.setting.item.category.setting", toolTipText = "app.setting.item.components.font.toolTipText", order = Ordered.HIGHEST_PRECEDENCE)
public class FontPanel extends JPanel implements Hintable {
    JLabel fontLabel;
    JComboBox<String> fontListBox;
    JLabel fontSizeLabel;
    JSpinner fontSizeSpinner;
    JLabel langLabel;
    JComboBox<String> langListBox;
    Set<String> fontList;

    public FontPanel(ResourceLoaderManager resources) {
        initComponents();
    }

    private void initComponents() {
        this.fontLabel = new JLabel();
        this.fontListBox = new JComboBox<>();
        this.fontSizeLabel = new JLabel();
        this.fontSizeSpinner = new JSpinner();
        this.langLabel = new JLabel();
        this.langListBox = new JComboBox<>();
        Font currentFont = UIManager.getFont("Label.font");
        String currentFamily = currentFont.getFamily();
        String currentSize = Integer.toString(currentFont.getSize());
        fontList = ThemeUIManager.getAvailableFontFamilyNames();

        setLayout(new MigLayout(
                "insets dialog,hidemode 3",
                "[][][]",
                ""
        ));

        //---- fontLabel ----
        fontLabel.setText(I18nHelper.getMessage("app.setting.item.components.font.fontLabel") + ":");
        fontLabel.setDisplayedMnemonic('C');
        fontLabel.setLabelFor(fontListBox);
        fontListBox.setEditable(true);
        fontListBox.addActionListener(this::fontFamilyChanged);


        fontListBox.setModel(new DefaultComboBoxModel<>(fontList.toArray(String[]::new)));
        fontListBox.setSelectedItem(currentFamily);
        add(fontLabel, "cell 0 4");
        add(fontListBox, "cell 1 4,growx,width 200:300:");


        fontSizeLabel.setText(I18nHelper.getMessage("app.setting.item.components.font.fontSizeLabel") + ":");
        fontSizeLabel.setLabelFor(fontSizeSpinner);
        fontSizeLabel.setDisplayedMnemonic('S');
        add(fontSizeLabel, "cell 0 5");
        fontSizeSpinner.setModel(new SpinnerNumberModel(Integer.parseInt(currentSize), 1, 72, 1));
        fontSizeSpinner.addChangeListener(this::fontSizeChanged);
        add(fontSizeSpinner, "cell 1 5,growx,width 200:300:");

        langLabel.setText(I18nHelper.getMessage("app.setting.item.components.font.lang") + ":");
        langLabel.setLabelFor(langListBox);
        langLabel.setDisplayedMnemonic('L');
        langListBox.setEditable(true);
        String[] languagesModelArrays = I18nHelper.getAllSupportLanguages();
        langListBox.setModel(new DefaultComboBoxModel<>(languagesModelArrays));
        langListBox.addActionListener(this::languagesChanges);

        add(langLabel, "cell 0 6");
        add(langListBox, "cell 1 6,growx,width 200:300:");
    }

    private void languagesChanges(ActionEvent e) {
        String lang = (String) langListBox.getSelectedItem();
        if (I18nHelper.isCurrentLanguage(lang)) {
            langListBox.setSelectedItem(I18nHelper.getCurrentLocale());
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                FontPanel.this,
                I18nHelper.getMessage("app.setting.item.components.font.change.language.message", I18nHelper.getCurrentLocale().getLanguage(), lang),
                I18nHelper.getMessage("app.setting.item.components.font.change.language.title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        log.debug("Close dialog result: {}", result == JOptionPane.YES_OPTION ? "YES" : "NO/CANCEL");
        if (result != JOptionPane.YES_OPTION) {
            langListBox.setSelectedItem(I18nHelper.getCurrentLocale());
            return;
        }
        log.info("Language changed to: {}", lang);
        try {
            I18nHelper.updateLanguage(lang);
            I18nHelper.init();
            FrameManager.publishEvent(new RestartEvent(this));
        } catch (Exception ex) {
            log.error("Failed to restart application", ex);
            JOptionPane.showMessageDialog(null,
                    "Failed to restart application: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            langListBox.setSelectedItem(I18nHelper.getCurrentLocale());
        }
    }

    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = (String) fontListBox.getSelectedItem();
        ThemeUIManager.fontFamilyChanged(fontFamily);

    }

    private void fontSizeChanged(ChangeEvent e) {
        float size = ConvertUtil.toFloat(fontSizeSpinner.getValue());
        ThemeUIManager.fontSizeChanged(size);
    }

    public void showHint(boolean reload) {
        if (reload) {
            clearHint();
        }
        HintManager.Hint languageList = new HintManager.Hint(
                I18nHelper.getMessage("app.setting.font.panel.hint.language"),
                langListBox, SwingConstants.RIGHT, "setting.hint.language", null);

        HintManager.Hint fontSizeHint = new HintManager.Hint(
                I18nHelper.getMessage("app.setting.font.panel.hint.font.size"),
                fontSizeSpinner, SwingConstants.RIGHT, "setting.hint.font.size", languageList);

        HintManager.Hint fontList = new HintManager.Hint(
                I18nHelper.getMessage("app.setting.font.panel.hint.font.name"),
                fontListBox, SwingConstants.RIGHT, "setting.hint.font.name", fontSizeHint);

        HintManager.showHint(fontList);
    }

    @Override
    public void clearHint() {
        log.debug("clearHint show hint");
        JFramePref.state.remove("setting.hint.font.name");
        JFramePref.state.remove("setting.hint.font.size");
        JFramePref.state.remove("setting.hint.language");
    }

}
