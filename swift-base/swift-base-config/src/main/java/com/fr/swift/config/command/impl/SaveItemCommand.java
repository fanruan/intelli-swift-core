package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.oper.ConfigSession;

/**
 * @author yee
 * @date 2019-07-30
 */
class SaveItemCommand<T> implements SwiftConfigCommand<T> {
    private T obj;

    SaveItemCommand(T obj) {
        this.obj = obj;
    }

    @Override
    public T apply(ConfigSession p) {
        p.save(obj);
        return obj;
    }
}
