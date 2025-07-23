package cn.ximuli.jframex.ui.panels;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Getter
@Setter
@AllArgsConstructor
public class SettingInfo<T> {
    private String category;
    private String name;
    private String toolTipText;
    private Class<T> clz;
}
