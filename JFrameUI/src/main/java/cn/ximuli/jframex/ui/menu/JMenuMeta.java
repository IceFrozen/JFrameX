package cn.ximuli.jframex.ui.menu;


import java.awt.event.KeyEvent;
import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMenuMeta {
    String value() default "";
    String role() default Anyone;
    static String Anyone = "*";

    int shortKey() default -1;
}
