package cn.ximuli.jframex.ui.component.panels;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.util.function.Consumer;


@Getter
@Setter
@AllArgsConstructor
public class SettingInfo<T extends JComponent> {
    private String category;
    private String name;
    private String toolTipText;
    private Class<T> clz;
    private T value;
}
