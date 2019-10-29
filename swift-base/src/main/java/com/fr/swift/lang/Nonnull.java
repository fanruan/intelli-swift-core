package com.fr.swift.lang;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author anchore
 * @date 2019/3/26
 * <p>
 * IDEA: Settings > Editor > Inspections > Java > Probable bugs > @Nonnull/@Nullable Problems > Configure annotations
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.LOCAL_VARIABLE})
public @interface Nonnull {

    String value() default "";
}