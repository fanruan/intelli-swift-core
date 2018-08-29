package com.fr.swift.jdbc.invoke;

import com.fr.swift.jdbc.bean.CreateTableBean;

/**
 * @author yee
 * @date 2018/8/29
 */
public abstract class BaseCreateTableInvoke implements SqlInvoke<Integer> {

    protected CreateTableBean bean;

    public BaseCreateTableInvoke(CreateTableBean bean) {
        this.bean = bean;
    }

    @Override
    public Type getType() {
        return Type.CREATE_TABLE;
    }
}
