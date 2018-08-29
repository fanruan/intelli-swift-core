package com.fr.swift.jdbc.invoke;

import com.fr.swift.jdbc.bean.InsertBean;

/**
 * @author yee
 * @date 2018/8/29
 */
public abstract class BaseInsertInvoke implements SqlInvoke<Integer> {

    protected InsertBean insertBean;

    public BaseInsertInvoke(InsertBean insertBean) {
        this.insertBean = insertBean;
    }

    @Override
    public Type getType() {
        return Type.INSERT;
    }
}
