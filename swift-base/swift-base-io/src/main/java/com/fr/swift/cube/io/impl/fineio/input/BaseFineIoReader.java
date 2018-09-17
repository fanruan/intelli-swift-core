package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.io.Buffer;
import com.fineio.io.file.IOFile;
import com.fr.swift.cube.io.input.Reader;

/**
 * @author anchore
 */
abstract class BaseFineIoReader<Buf extends Buffer> implements Reader {
    IOFile<Buf> ioFile;

    @Override
    public boolean isReadable() {
        return ioFile != null && ioFile.exists();
    }

    /**
     * FineIO的reader不需要进行释放，这里是空实现
     */
    @Override
    public final void release() {
    }
}