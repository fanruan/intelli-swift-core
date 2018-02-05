package com.fr.swift.cube.io.impl.fineio.input;

import com.fr.swift.cube.io.input.Reader;

/**
 * @author anchore
 */
abstract class BaseFineIoReader implements Reader {

    /**
     * FineIO的reader不需要进行释放，这里是空实现
     */
    @Override
    public final void release() {
    }

}

