package cn.ximuli.jframex.ui.component.menu;


import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mate {
    int order() default 0;
    String value() default "";
    String role() default Anyone;
    String Anyone = "*";
    String icon() default "";
    String id() default "";
    int shortKey() default -1;
}
