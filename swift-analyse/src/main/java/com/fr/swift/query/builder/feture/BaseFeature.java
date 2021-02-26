package com.fr.swift.query.builder.feture;

import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.Segment;

/**
 * @author xiqiu
 * @date 2021/1/27
 * @description 查询加速特征，父类定义结构，子类做具体值的实现
 * @since swift-1.2.0
 */
public abstract class BaseFeature<T> {
    private boolean init = false;

    protected T t;
    protected Segment segment;
    protected SwiftDetailFilterInfo detailFilterInfo;

    public BaseFeature(Segment segment, SwiftDetailFilterInfo detailFilterInfo) {
        this.segment = segment;
        this.detailFilterInfo = detailFilterInfo;
    }

    public T getValue() {
        if (!init) {
            setValue();
        }
        return t;
    }

    public void setValue() {
        init = true;
        doSetValue();
    }

    /**
     * 设计成这种模式是为了延迟加载，特征都准备好，有些操作是比较耗时的，但是没有用到的话就不会去真正计算，而且进行了封装
     */
    public abstract void doSetValue();
}
