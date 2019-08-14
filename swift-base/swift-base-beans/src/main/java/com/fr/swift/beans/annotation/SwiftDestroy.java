package com.fr.swift.beans.annotation;

import java.lang.annotation.*;

/**
 * @author anner
 * @this annotation created on date 2019/8/9
 * @description 销毁后的destory方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SwiftDestroy {
}
