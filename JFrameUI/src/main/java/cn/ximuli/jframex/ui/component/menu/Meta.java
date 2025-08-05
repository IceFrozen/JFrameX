package cn.ximuli.jframex.ui.component.menu;


import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Meta {
    int order() default 0;
    String value() default "";
    String icon() default "";
    String id() default "";
    int shortKey() default -1;
}
