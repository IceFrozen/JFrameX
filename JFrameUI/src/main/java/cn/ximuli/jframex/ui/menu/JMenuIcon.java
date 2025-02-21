package cn.ximuli.jframex.ui.menu;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMenuIcon {
    String value() default "";
}
