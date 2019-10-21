package com.fr.swift.executor.type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class created on 2019/5/13
 *
 * @author Lucifer
 * @description
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskType {

    Class<? extends Enum> type() default SwiftTaskType.class;

}
