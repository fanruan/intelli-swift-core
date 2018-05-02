package com.fr.swift.query.group.by2;

import java.util.List;

/**
 * Created by Lyon on 2018/4/23.
 */
public interface GroupByController<T> {

    /**
     * expander控制groupBy的接口
     * 检查是否为groupBy的一行，如果必要，通过回调方法PopUpCallback#popUp()控制groupBy迭代器
     *
     * @param entries
     * @return
     */
    boolean isRow(List<T> entries, PopUpCallback callback);
}
