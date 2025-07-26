package cn.ximuli.jframex.ui.component.panels;


import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SettingMenu {
    String category() default "";
    String value() default "";
    String toolTipText() default "";
    int order() default 0;
}
