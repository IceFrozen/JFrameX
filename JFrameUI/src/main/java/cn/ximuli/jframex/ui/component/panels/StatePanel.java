package cn.ximuli.jframex.ui.component.panels;

import cn.ximuli.jframex.common.utils.DateUtil;
import cn.ximuli.jframex.model.LoggedInUser;
import cn.ximuli.jframex.ui.I18nHelper;
import cn.ximuli.jframex.ui.storage.JFramePref;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

import static javax.swing.border.EtchedBorder.RAISED;

@Slf4j
public class StatePanel extends JPanel {
    LoggedInUser user;
    JLabel stateLabel = null;
    private JLabel nameLabel = null;
    private JLabel nowDateLabel = null;
    private JSeparator jSeparator1 = null;
    private static JLabel czyStateLabel = null;
    private JSeparator jSeparator2 = null;

    private JInternalFrame currentFrame;

    private String statusPanelDefaultContext;


    public StatePanel(LoggedInUser user) {
        this.user = user;
        init();
    }

    public void init() {
        GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
        gridBagConstraints6.gridx = 2;
        gridBagConstraints6.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints6.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints6.gridy = 0;
        GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
        gridBagConstraints4.gridx = 3;
        gridBagConstraints4.gridy = 0;
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 6;
        gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints3.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints3.gridy = 0;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 5;
        gridBagConstraints11.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints11.gridy = 0;
        nowDateLabel = new JLabel();
        nowDateLabel.setText(DateUtil.formatTime(LocalDateTime.now(), "yyyy-MM-dd"));
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 7;
        gridBagConstraints2.weightx = 0.0;
        gridBagConstraints2.fill = GridBagConstraints.NONE;
        gridBagConstraints2.gridy = 0;
        nameLabel = new JLabel(I18nHelper.getMessage("app.mainframe.state.co"));
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 4;
        gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints1.weighty = 1.0;
        gridBagConstraints1.insets = new Insets(0, 5, 0, 5);
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridy = 0;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createBevelBorder(RAISED));
        add(getStateLabel(), gridBagConstraints);
        add(getJSeparator(), gridBagConstraints1);
        add(nameLabel, gridBagConstraints2);
        add(getJSeparator1(), gridBagConstraints3);
        add(nowDateLabel, gridBagConstraints11);
        add(getCzyStateLabel(), gridBagConstraints4);
        add(getJSeparator2(), gridBagConstraints6);
        statusPanelDefaultContext = I18nHelper.getMessage("app.mainframe.state.selected");

    }

    public static JLabel getCzyStateLabel() {
        LoggedInUser user = JFramePref.getUser();
        String userName = "Unknown";
        if (user != null) {
            userName = user.getUsername();
        }
        if (czyStateLabel == null) {
            czyStateLabel = new JLabel(I18nHelper.getMessage("app.mainframe.state.operator") + ":" + userName);
        }
        return czyStateLabel;
    }

    public JLabel getStateLabel() {
        if (stateLabel == null) {
            stateLabel = new JLabel();
            stateLabel.setText(I18nHelper.getMessage("app.mainframe.state.selected"));
        }
        return stateLabel;
    }

    private JSeparator getJSeparator() {
        JSeparator jSeparator = new JSeparator();
        jSeparator.setOrientation(JSeparator.VERTICAL);
        return jSeparator;
    }

    private JSeparator getJSeparator1() {
        if (jSeparator1 == null) {
            jSeparator1 = new JSeparator();
            jSeparator1.setOrientation(SwingConstants.VERTICAL);
        }
        return jSeparator1;
    }


    private JSeparator getJSeparator2() {
        if (jSeparator2 == null) {
            jSeparator2 = new JSeparator();
            jSeparator2.setOrientation(SwingConstants.VERTICAL);
        }
        return jSeparator2;
    }

    public void setStateLabelText(String text) {
        this.stateLabel.setText(text);
    }

    public void frameSelected(JInternalFrame frame) {
        this.currentFrame = frame;
        String currentContext = "";
        if (this.currentFrame == null) {
            currentContext = statusPanelDefaultContext;
        } else {
            currentContext = frame.getTitle();
        }
        String finalCurrentContext = currentContext;
        SwingUtilities.invokeLater(() -> this.setStateLabelText(finalCurrentContext));
    }

    public boolean isFrameSelected(JInternalFrame frame) {
        return frame == this.currentFrame;
    }
}
