package cn.ximuli.jframex.ui.component.panels;
import cn.ximuli.jframex.common.utils.ConvertUtil;
import cn.ximuli.jframex.common.utils.RestartUtil;
import cn.ximuli.jframex.common.utils.StringUtil;
import cn.ximuli.jframex.ui.Application;
import cn.ximuli.jframex.ui.I18nHelper;

import cn.ximuli.jframex.ui.storage.JFramePref;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.util.FontUtils;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@Slf4j
@SettingMenu(value = "app.setting.item.components.font", category = "app.setting.item.category.setting", toolTipText = "app.setting.item.components.font.toolTipText", order = Ordered.HIGHEST_PRECEDENCE)
public class FontPanel extends JPanel {
    JLabel fontLabel;
    JComboBox<String> fontListBox;
    JLabel fontSizeLabel;
    JSpinner fontSizeSpinner;
    JLabel langLabel;
    JComboBox<String> langListBox;
    List<String> fontList;

    public FontPanel() {
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

        String[] availableFontFamilyNames = FontUtils.getAvailableFontFamilyNames().clone();
        Arrays.sort(availableFontFamilyNames);

        fontList = new ArrayList<>();
        List<String> families = new ArrayList<>(Arrays.asList(
                "Arial", "Cantarell", "Comic Sans MS", "DejaVu Sans",
                "Dialog", "Inter", "Liberation Sans", "Noto Sans", "Open Sans", "Roboto",
                "SansSerif", "Segoe UI", "Serif", "Tahoma", "Ubuntu", "Verdana"));
        families.addAll(Arrays.asList(availableFontFamilyNames));
        families.sort(String.CASE_INSENSITIVE_ORDER);
        fontList.add(currentFamily);
        families.remove(currentFamily);
        fontList.addAll(families);

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
        add(fontLabel, "cell 0 4");
        add(fontListBox, "cell 1 4,growx,width 200:300:"); // 最小200px，首选300px，无最大限制


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
        String[] languagesModelArrays = Arrays.stream(I18nHelper.SUPPORT_LANGUAGES).map(Locale::getLanguage).toArray(String[]::new);
        langListBox.setModel(new DefaultComboBoxModel<>(languagesModelArrays));
        langListBox.addActionListener(this::languagesChanges);

        add(langLabel, "cell 0 6");
        add(langListBox, "cell 1 6,growx,width 200:300:");
    }

    private void languagesChanges(ActionEvent e) {
        String lang = (String) langListBox.getSelectedItem();
        String currentLang = System.getenv(Application.APP_LANGUAGE);
        if (StringUtil.isNotBlank(lang) && !lang.equals(currentLang)) {
            log.info("Language changed to: {}", lang);
            JFramePref.state.put(Application.APP_LANGUAGE, lang);
            // 自定义按钮标签
            Object[] options = {"Restart Now", "Cancel"};
            int i = JOptionPane.showOptionDialog(
                    this,
                    "Language changed to " + lang + ". Please restart the application to apply changes.",
                    "Title",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);


            if (i == JOptionPane.OK_OPTION) {

                try {
                    RestartUtil.restartApplication();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Failed to restart application: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    private void fontFamilyChanged(ActionEvent e) {
        String fontFamily = (String) fontListBox.getSelectedItem();
        FlatAnimatedLafChange.showSnapshot();
        Font font = UIManager.getFont("defaultFont");
        Font newFont = FontUtils.getCompositeFont(fontFamily, font.getStyle(), font.getSize());
        UIManager.put("defaultFont", newFont);

        FlatLaf.updateUI();
        FlatAnimatedLafChange.hideSnapshotWithAnimation();


    }

    private void fontSizeChanged(ChangeEvent e) {
        int size = ConvertUtil.toInt(fontSizeSpinner.getValue());
        Font font = UIManager.getFont("defaultFont");
        Font newFont = font.deriveFont((float) size);
        UIManager.put("defaultFont", newFont);
        FlatLaf.updateUI();
    }
}
