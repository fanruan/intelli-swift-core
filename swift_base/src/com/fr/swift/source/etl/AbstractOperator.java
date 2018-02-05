package com.fr.swift.source.etl;

import com.fr.swift.source.core.Core;
import com.fr.swift.source.core.CoreGenerator;

/**
 * Created by pony on 2018/1/5.
 */
public abstract class AbstractOperator implements ETLOperator {
    private Core core;

    @Override
    public Core fetchObjectCore() {
        if (core == null) {
            core = new CoreGenerator(this).fetchObjectCore();
        }
        return core;
    }
}
