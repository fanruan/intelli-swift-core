package com.fr.bi.common.factory.annotation;

import com.fr.bi.common.factory.IModuleFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明当前对象准许进行托管
 * Created by Connery on 2015/12/8.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BIMandatedObject {
    String module() default IModuleFactory.CONF_MODULE;

    /**
     * 决定交给哪个factory实例
     *
     * @return
     */
    String factory() default "default_factory";

    /**
     * 注册的key值。如果和impliment也指定了
     * 那么会注册两个。
     *
     * @return
     */
    String key() default "default_key";

    /**
     * 是当前被标签标注的Class实现的接口或者继承的父类
     * 例如：MySQLManger 和 HSQLManager 分别
     * 继承或者实现DBManager，那么MySQLManger和
     * HSQLManager的implement应该为DBManager，
     * 在实际使用中也是通过DBManager来获得具体的对象
     * 同一子类必须指定同一父类，或者说同一行为的对象
     * 必须指定同一接口。否则在不同的Factory中可能注册不同的
     * key值，那么行为改变，factory需要替换的时候，会找不到对象。
     */
    Class implement() default Object.class;
}