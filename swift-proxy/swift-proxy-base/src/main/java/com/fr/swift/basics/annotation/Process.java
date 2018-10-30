package com.fr.swift.basics.annotation;



import com.fr.swift.basics.ProcessHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打在ProcessHandler上或者ProcessHandler实现类上
 * @author yee
 * @date 2018/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Process {
    /**
     * 需要填ProcessHandler的接口，每种ProcessHandler 都需要写一个接口
     * 如请求Master节点的ProcessHandler，inf 填 MasterProcessHandler.class
     * @return
     */
    Class<? extends ProcessHandler>[] inf();
}
