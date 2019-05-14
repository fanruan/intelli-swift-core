package com.fr.swift.base.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yee
 * @date 2018-12-04
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface JsonTypeInfo {
    JsonTypeInfo.Id use();

    JsonTypeInfo.As include() default JsonTypeInfo.As.PROPERTY;

    String property() default "";

    Class<?> defaultImpl() default JsonTypeInfo.None.class;

    boolean visible() default false;

    enum As {
        PROPERTY,
        WRAPPER_OBJECT,
        WRAPPER_ARRAY,
        EXTERNAL_PROPERTY,
        EXISTING_PROPERTY;

        As() {
        }
    }

    enum Id {
        NONE(null),
        CLASS("@class"),
        MINIMAL_CLASS("@c"),
        NAME("@type"),
        CUSTOM(null);

        private final String _defaultPropertyName;

        Id(String defProp) {
            this._defaultPropertyName = defProp;
        }

        public String getDefaultPropertyName() {
            return this._defaultPropertyName;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    abstract class None {
        public None() {
        }
    }
}
