package com.fr.swift.cube.io.impl.fineio.input;

import com.fineio.accessor.buffer.Buf;
import com.fineio.accessor.file.IReadFile;
import com.fr.swift.cube.io.input.Reader;
import com.fr.swift.util.IoUtil;

/**
 * @author anchore
 */
abstract class BaseFineIoReader<B extends Buf> implements Reader {
    IReadFile<B> readFile;

    @Override
    public boolean isReadable() {
        return readFile != null && readFile.exists();
    }

    @Override
    public final void release() {
        IoUtil.close(readFile);
    }
}