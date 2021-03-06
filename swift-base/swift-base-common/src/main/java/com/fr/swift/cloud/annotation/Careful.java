package com.fr.swift.cloud.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2019/5/28
 *
 * @author Lucifer
 * @description 用于标记一个类、方法或者字段，在修改的时候必须非常慎重，要考虑前后端、移动端的兼容性以及性能等问题
 */
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE
})
@Documented
public @interface Careful {
}
