package cn.ximuli.jframex.ui.menu;


import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JMenuMeta {
    int order() default 0;
    String value() default "";
    String role() default Anyone;
    String Anyone = "*";
    String icon() default "";

    int shortKey() default -1;
}
